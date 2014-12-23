package tl.lin.lucene;

import java.io.IOException;
import java.util.Collection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

public class HdfsReadOnlyDirectory extends Directory {
  private final FileSystem fs;
  private final Path directory;
  private final int ioFileBufferSize;

  public HdfsReadOnlyDirectory(Configuration conf, Path directory) throws IOException {
    this.fs = FileSystem.get(conf);
    this.directory = directory;
    this.ioFileBufferSize = conf.getInt("io.file.buffer.size", 4096);
  }

  @Override
  public String[] listAll() throws IOException {
    FileStatus[] fileStatus = fs.listStatus(directory);
    String[] result = new String[fileStatus.length];
    for (int i = 0; i < fileStatus.length; i++) {
      result[i] = fileStatus[i].getPath().getName();
    }
    return result;
  }

  @Override
  public boolean fileExists(String name) throws IOException {
    return fs.exists(new Path(directory, name));
  }

  @Override
  public long fileLength(String name) throws IOException {
    return fs.getFileStatus(new Path(directory, name)).getLen();
  }

  @Override
  public IndexInput openInput(String name, IOContext context) throws IOException {
    return new FileSystemIndexInput(new Path(directory, name), ioFileBufferSize);
  }

  @Override
  public Lock makeLock(final String name) {
    return new Lock() {
      @Override
      public boolean obtain() {
        return true;
      }

      @Override
      public boolean isLocked() {
        throw new UnsupportedOperationException();
      }

      @Override
      public String toString() {
        return "Lock@" + new Path(directory, name);
      }

      @Override
      public void close() throws IOException {
      }
    };
  }

  @Override
  public void close() throws IOException {
    // do not close the file system
  }

  @Override
  public String toString() {
    return this.getClass().getName() + "@" + directory;
  }

  private class FileSystemIndexInput extends BufferedIndexInput {
    // shared by clones
    private class Descriptor {
      public final FSDataInputStream in;
      public long position; // cache of in.getPos()

      public Descriptor(Path file, int ioFileBufferSize) throws IOException {
        this.in = fs.open(file, ioFileBufferSize);
      }
    }

    private final Path filePath; // for debugging
    private final Descriptor descriptor;
    private final long length;
    private boolean isOpen;
    private boolean isClone;

    public FileSystemIndexInput(Path path, int ioFileBufferSize) throws IOException {
      super(path.toString());
      filePath = path;
      descriptor = new Descriptor(path, ioFileBufferSize);
      length = fs.getFileStatus(path).getLen();
      isOpen = true;
    }

    protected void readInternal(byte[] b, int offset, int len) throws IOException {
      synchronized (descriptor) {
        long position = getFilePointer();
        if (position != descriptor.position) {
          descriptor.in.seek(position);
          descriptor.position = position;
        }
        int total = 0;
        do {
          int i = descriptor.in.read(b, offset + total, len - total);
          if (i == -1) {
            throw new IOException("Read past EOF");
          }
          descriptor.position += i;
          total += i;
        } while (total < len);
      }
    }

    public void close() throws IOException {
      if (!isClone) {
        if (isOpen) {
          descriptor.in.close();
          isOpen = false;
        } else {
          throw new IOException("Index file " + filePath + " already closed");
        }
      }
    }

    protected void seekInternal(long position) {
      // handled in readInternal()
    }

    public long length() {
      return length;
    }

    protected void finalize() throws IOException {
      if (!isClone && isOpen) {
        close(); // close the file
      }
    }

    public BufferedIndexInput clone() {
      FileSystemIndexInput clone = (FileSystemIndexInput) super.clone();
      clone.isClone = true;
      return clone;
    }
  }

  @Override
  public void clearLock(String name) throws IOException {
  }

  @Override
  public IndexOutput createOutput(String name, IOContext context) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public LockFactory getLockFactory() {
    return null;
  }

  @Override
  public void setLockFactory(LockFactory lockFactory) throws IOException {
  }

  @Override
  public void sync(Collection<String> names) throws IOException {
  }

  @Override
  public void deleteFile(String name) throws IOException {
    throw new UnsupportedOperationException();
  }
}
