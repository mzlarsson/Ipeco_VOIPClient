package se.chalmers.fleetspeak.command;

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
		 * @return the name of the command.
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the correct format for use of the command.
		 */
		public String getFormat() {
			return format;
		}

		/**
		 * @return the description of the command.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @return the execCode to ease linking this info object to the actual Command.
		 */
		public int getExecCode() {
			return execCode;
		}
}
