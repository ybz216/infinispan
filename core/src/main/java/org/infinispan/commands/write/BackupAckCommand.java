package org.infinispan.commands.write;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.infinispan.commands.InitializableCommand;
import org.infinispan.commands.remote.BaseRpcCommand;
import org.infinispan.factories.ComponentRegistry;
import org.infinispan.util.ByteString;
import org.infinispan.util.concurrent.CommandAckCollector;

/**
 * A command that represents an acknowledge sent by a backup owner to the originator.
 * <p>
 * The acknowledge signals a successful execution of the operation.
 *
 * @author Pedro Ruivo
 * @since 9.0
 */
public class BackupAckCommand extends BaseRpcCommand implements InitializableCommand {

   public static final byte COMMAND_ID = 2;
   private CommandAckCollector commandAckCollector;
   private long id;
   private int topologyId;

   public BackupAckCommand() {
      super(null);
   }

   public BackupAckCommand(ByteString cacheName) {
      super(cacheName);
   }

   public BackupAckCommand(ByteString cacheName, long id, int topologyId) {
      super(cacheName);
      this.id = id;
      this.topologyId = topologyId;
   }

   @Override
   public void init(ComponentRegistry componentRegistry, boolean isRemote) {
      this.commandAckCollector = componentRegistry.getCommandAckCollector().running();
   }

   public void ack() {
      commandAckCollector.backupAck(id, getOrigin(), topologyId);
   }

   @Override
   public byte getCommandId() {
      return COMMAND_ID;
   }

   @Override
   public boolean isReturnValueExpected() {
      return false;
   }

   @Override
   public boolean canBlock() {
      return false;
   }

   @Override
   public void writeTo(ObjectOutput output) throws IOException {
      output.writeLong(id);
      output.writeInt(topologyId);
   }

   @Override
   public void readFrom(ObjectInput input) throws IOException, ClassNotFoundException {
      id = input.readLong();
      topologyId = input.readInt();
   }

   @Override
   public String toString() {
      return "BackupAckCommand{" +
            "id=" + id +
            ", topologyId=" + topologyId +
            '}';
   }
}
