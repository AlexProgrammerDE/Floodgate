package org.geysermc.floodgate.core.command.linkedaccounts;

import static org.geysermc.floodgate.core.command.linkedaccounts.LinkedAccountsCommand.linkInfoMessage;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.core.command.CommonCommandMessage;
import org.geysermc.floodgate.core.command.LinkAccountCommand;
import org.geysermc.floodgate.core.command.util.Permission;
import org.geysermc.floodgate.core.connection.audience.ProfileAudience;
import org.geysermc.floodgate.core.connection.audience.UserAudience;
import org.geysermc.floodgate.core.http.ProfileFetcher;
import org.geysermc.floodgate.core.link.LocalPlayerLinking;
import org.geysermc.floodgate.core.platform.command.FloodgateSubCommand;
import org.geysermc.floodgate.core.util.Constants;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;

@Singleton
final class AddLinkedAccountCommand extends FloodgateSubCommand {
    @Inject Optional<LocalPlayerLinking> optionalLinking;
    @Inject ProfileFetcher fetcher;
    @Inject FloodgateLogger logger;

    AddLinkedAccountCommand() {
        super(LinkedAccountsCommand.class, "add", "Manually add a locally linked account", Permission.COMMAND_LINKED_MANAGE, "a");
    }

    @Override
    public Command.Builder<UserAudience> onBuild(Command.Builder<UserAudience> commandBuilder) {
        return super.onBuild(commandBuilder)
                .argument(ProfileAudience.ofAnyIdentifierBedrock("bedrock"))
                .argument(ProfileAudience.ofAnyIdentifierJava("java"));
    }

    @Override
    public void execute(CommandContext<UserAudience> context) {
        UserAudience sender = context.sender();

        if (optionalLinking.isEmpty()) {
            sender.sendMessage(CommonCommandMessage.LINKING_DISABLED);
            return;
        }

        var linking = optionalLinking.get();
        if (linking.state().globalLinkingEnabled()) {
            sender.sendMessage(CommonCommandMessage.LOCAL_LINKING_NOTICE, Constants.LINK_INFO_URL);
        }

        ProfileAudience bedrockInput = context.get("bedrock");
        ProfileAudience javaInput = context.get("java");
        AtomicReference<ProfileAudience> bedrockRef = new AtomicReference<>(bedrockInput);
        AtomicReference<ProfileAudience> javaRef = new AtomicReference<>(javaInput);

        var futures = new ArrayList<CompletableFuture<?>>();

        if (bedrockRef.get().uuid() == null) {
            futures.add(fetcher.fetchXuidFor(bedrockRef.get().username()).thenAccept(bedrockRef::set));
        }
        if (javaRef.get().uuid() == null) {
            futures.add(fetcher.fetchUniqueIdFor(javaRef.get().username()).thenAccept(javaRef::set));
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenAccept($ -> {
                    var bedrock = bedrockRef.get();
                    var java = javaRef.get();

                    if (bedrock == null) {
                        sender.sendMessage("Could not find Bedrock account with username " + bedrockInput.username());
                    }
                    if (java == null) {
                        sender.sendMessage("Could not find Java account with username " + javaInput.username());
                    }

                    linking.addLink(java.uuid(), java.username(), bedrock.uuid()).whenComplete((player, throwable) -> {
                        if (throwable != null) {
                            sender.sendMessage(LinkAccountCommand.Message.LINK_REQUEST_ERROR);
                            logger.error("Exception while manually linking accounts", throwable);
                            return;
                        }
                        sender.sendMessage("You've successfully linked:\n" + linkInfoMessage(player));
                    });
                });
    }
}
