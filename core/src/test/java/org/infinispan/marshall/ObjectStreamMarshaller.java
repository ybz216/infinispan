package org.infinispan.marshall;

import org.infinispan.io.ByteBuffer;
import org.infinispan.util.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * A dummy marshaller impl that uses JDK object streams
 *
 * @author Manik Surtani
 */
public class ObjectStreamMarshaller implements Marshaller, Serializable {
   
   public ObjectOutput startObjectOutput(OutputStream os) throws IOException {
      return new ObjectOutputStream(os);
   }

   public void finishObjectOutput(ObjectOutput oo) {
      Util.flushAndCloseOutput(oo);
   }
   
   public void objectToObjectStream(Object obj, ObjectOutput out) throws IOException {
      out.writeObject(obj);
   }

   public Object objectFromObjectStream(ObjectInput in) throws IOException, ClassNotFoundException {
      return in.readObject();
   }

   public ObjectInput startObjectInput(InputStream is) throws IOException {
      return new ObjectInputStream(is);
   }

   public void finishObjectInput(ObjectInput oi) {
      Util.closeInput(oi);
   }

   public Object objectFromStream(InputStream is) throws IOException, ClassNotFoundException {
      if (is instanceof ObjectInputStream)
         return objectFromObjectStream((ObjectInputStream) is);
      else
         return objectFromObjectStream(new ObjectInputStream(is));
   }

   public ByteBuffer objectToBuffer(Object o) throws IOException {
      byte[] b = objectToByteBuffer(o);
      return new ByteBuffer(b, 0, b.length);
   }

   public Object objectFromByteBuffer(byte[] buf, int offset, int length) throws IOException, ClassNotFoundException {
      byte[] newBytes = new byte[length];
      System.arraycopy(buf, offset, newBytes, 0, length);
      return objectFromByteBuffer(newBytes);
   }

   public byte[] objectToByteBuffer(Object obj) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      objectToObjectStream(obj, oos);
      oos.flush();
      oos.close();
      baos.close();
      return baos.toByteArray();
   }

   public Object objectFromByteBuffer(byte[] buf) throws IOException, ClassNotFoundException {
      return objectFromObjectStream(new ObjectInputStream(new ByteArrayInputStream(buf)));
   }
}
