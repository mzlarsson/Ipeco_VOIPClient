package se.chalmers.fleetspeak.core.command.impl;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CommandFactory {
	
	private static ICommand[] commands;

	/**
	 * Creates all commands by using reflection.
	 * NOTE: This is only effective ONE time
	 * @return An array of all the available commands
	 */
	@SuppressWarnings("unchecked")
	public static ICommand[] createCommands(){
		if(CommandFactory.commands == null){
			List<ICommand> commandList = new ArrayList<ICommand>();
			List<Class<?>> classes = getCommandClasses();
			for(int i = 0; i<classes.size(); i++){
				try {
					Constructor<ICommand> constr = (Constructor<ICommand>)classes.get(i).getConstructor(int.class);
					commandList.add(constr.newInstance(i));
				} catch (InstantiationException e) {
					System.out.println("Could not initiate class: "+classes.get(i).getCanonicalName());
				} catch (IllegalAccessException e) {
					System.out.println("Could not access class: "+classes.get(i).getCanonicalName());
				} catch (NoSuchMethodException e) {
					try {
						commandList.add((ICommand)classes.get(i).newInstance());
					} catch (InstantiationException e1) {
						System.out.println("Could not initiate class: "+classes.get(i).getCanonicalName());
					} catch (IllegalAccessException e1) {
						System.out.println("Could not access class: "+classes.get(i).getCanonicalName());
					}
				} catch (SecurityException e) {
					System.out.println("Could not create instance of "+classes.get(i).getCanonicalName()+": "+e.getMessage());
				} catch (IllegalArgumentException e) {
					System.out.println("Invalid argument while instanceing command: "+classes.get(i).getCanonicalName());
				} catch (InvocationTargetException e) {
					System.out.println("InvocationTargetException while creating class "+classes.get(i).getCanonicalName());
				}
			}
			
			commands = new ICommand[commandList.size()];
			commands = commandList.toArray(commands);
		}
		
		return commands;
	}
	
	/**
	 * Collects the classes in this package that inherits the ICommand interface
	 * @return A list of classes containing all commands
	 */
	private static List<Class<?>> getCommandClasses(){
		List<Class<?>> classes = new ArrayList<Class<?>>();
		String packageName = getPackageName();
		
		File commandsFolder = new File("bin/"+packageName.replaceAll("\\.", "/"));
		if(commandsFolder.exists()){
			String clsName = null;
			Class<?> cls = null;
			for(File command : commandsFolder.listFiles()){
				if(command.getName().endsWith(".class")){
					clsName = command.getName().substring(0, command.getName().lastIndexOf("."));
					try {
						cls = Class.forName(packageName+"."+clsName);
						if(!cls.isInterface() && !Modifier.toString(cls.getModifiers()).contains("abstract")){
							for(Type type : cls.getGenericInterfaces()){
								if(type.getTypeName().equals(packageName+".ICommand")){
									classes.add(cls);
									break;
								}
							}
						}
					} catch (ClassNotFoundException e) {
						System.out.println("Class not found: "+clsName);
					}
				}
			}
		}
		
		return classes;
	}
	
	/**
	 * Retrieves the canonical name of this package
	 * @return The name of this package (by java dot standard)
	 */
	private static String getPackageName(){
		String factoryName = CommandFactory.class.getCanonicalName();
		return factoryName.substring(0, factoryName.lastIndexOf("."));
	}
}
