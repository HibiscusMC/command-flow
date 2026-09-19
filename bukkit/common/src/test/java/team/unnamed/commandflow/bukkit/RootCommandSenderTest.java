package team.unnamed.commandflow.bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.Test;
import team.unnamed.commandflow.Namespace;
import team.unnamed.commandflow.SimpleCommandManager;
import team.unnamed.commandflow.annotated.AnnotatedCommandTreeBuilder;
import team.unnamed.commandflow.annotated.CommandClass;
import team.unnamed.commandflow.annotated.annotation.Command;
import team.unnamed.commandflow.annotated.annotation.Sender;
import team.unnamed.commandflow.annotated.part.PartInjector;
import team.unnamed.commandflow.annotated.part.defaults.DefaultsModule;
import team.unnamed.commandflow.bukkit.factory.BukkitModule;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RootCommandSenderTest {

    @Test
    void executesRootCommandForConsoleSender() {
        RootCommand command = new RootCommand();
        PartInjector injector = PartInjector.create();
        injector.install(new DefaultsModule());
        injector.install(new BukkitModule());
        SimpleCommandManager manager = new SimpleCommandManager();
        manager.registerCommands(AnnotatedCommandTreeBuilder.create(injector).fromClass(command));

        ConsoleCommandSender console = (ConsoleCommandSender) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{ConsoleCommandSender.class},
                (proxy, method, arguments) -> null
        );
        Namespace namespace = Namespace.create();
        namespace.setObject(CommandSender.class, BukkitCommonConstants.SENDER_NAMESPACE, console);

        assertTrue(manager.execute(namespace, "root"));
        assertSame(console, command.sender);
    }

    @Command(names = "root")
    static class RootCommand implements CommandClass {
        private CommandSender sender;

        @Command(names = "")
        void run(@Sender CommandSender sender) {
            this.sender = sender;
        }
    }
}
