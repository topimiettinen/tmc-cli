package fi.helsinki.cs.tmc.cli.command;

import fi.helsinki.cs.tmc.cli.Application;

import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class creates a map for commands.
 * The code below may blow your mind.
 * Proceed at your own risk.
 */
public class CommandFactory {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommandFactory.class);
    private Map<String, Class> commands;

    public CommandFactory() {
        this.commands = new HashMap<>();
        createCommands();
        commandDiscoverer();
    }

    private void commandDiscoverer() {
        CommandAnnotationProcessor processor = new CommandAnnotationProcessor();
        System.out.println(processor.getCommands());
    }

    private void createCommands() {
        createCommand(TestCommand.class);
        createCommand(HelpCommand.class);
        createCommand(ListCoursesCommand.class);
        createCommand(ListExercisesCommand.class);
        createCommand(LoginCommand.class);
        createCommand(DownloadExercisesCommand.class);
        createCommand(SubmitCommand.class);
    }

    private void createCommand(Class commandClass) {
        Annotation annotation = commandClass.getAnnotation(Command.class);
        Command command = (Command)annotation;
        this.commands.put(command.name(), commandClass);
    }

    public CommandInterface getCommand(Application app, String name)  {
        Class commandClass = commands.get(name);
        Constructor<?> cons;
        if (commandClass == null) {
            return null;
        }
        try {
            cons = commandClass.getConstructor(Application.class);
        } catch (NoSuchMethodException ex) {
            logger.error("Every command MUST have constructor that takes "
                    + "Application object as argument", ex);
            System.exit(1);
            return null;
        } catch (SecurityException ex) {
            logger.error("getCommand failed ", ex);
            return null;
        }
        try {
            return (CommandInterface)cons.newInstance(app);
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("getCommand failed", ex);
        }
        return null;
    }

    public List<Command> getCommands() {
        List<Command> list = new ArrayList<>();
        for (Class command : this.commands.values()) {
            Annotation annotation = command.getAnnotation(Command.class);
            list.add((Command)annotation);
        }
        return list;
    }
}
