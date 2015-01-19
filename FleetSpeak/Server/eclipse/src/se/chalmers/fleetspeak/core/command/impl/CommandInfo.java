package se.chalmers.fleetspeak.core.command.impl;

/**
 * An immutable class that holds the information about an available Command.
 * @author Patrik Haar
 *
 */
public class CommandInfo {

		private String name, format, description;
		private int execCode;
		
		/**
		 * Create and info object about an available data.
		 * @param name The name of the command.
		 * @param format The correct format for use of the command.
		 * @param desc A description about the command.
		 * @param exCode An optimization used for faster execution.
		 */
		public CommandInfo(String name, String format, String desc, int exCode){
			this.name = name;
			this.format = format;
			this.description = desc;
			this.execCode = exCode;
		}

		/**
		 * The name of the command, for example:
		 * "ExampleCommand"
		 * @return the name of the command.
		 */
		public String getName() {
			return name;
		}

		/**
		 * The format of the command, for example:
		 * "ExampleCommand [param1] [param2]"
		 * @return the correct format for use of the command.
		 */
		public String getFormat() {
			return format;
		}

		/**
		 * The description of the command, for example:
		 * "Executes test-function number 5"
		 * @return the description of the command.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * The code linking this info object to its command, this is used in
		 * optimizations and ease of handling.
		 * @return the execution code linking this info object to the actual Command.
		 */
		public int getExecCode() {
			return execCode;
		}
		
		@Override
		public CommandInfo clone() {
			return new CommandInfo(getName(), getFormat(), getDescription(), getExecCode());
		}
}
