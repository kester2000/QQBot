package org.Kester.QQBot;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.action.MemberNudge;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JavaMain extends JavaPlugin {
    public static final JavaMain INSTANCE = new JavaMain();

    private JavaMain() {
        super(new JvmPluginDescriptionBuilder("org.Kester.QQBot", "1.0").build());
    }

    @Override
    public void onEnable() {
        getLogger().info("日志");
        EventChannel<Event> eventChannel = GlobalEventChannel.INSTANCE.parentScope(this);
        GlobalEventChannel.INSTANCE.subscribeAlways(MessageEvent.class, (MessageEvent messageEvent) -> {
            if (messageEvent.getMessage().contentToString().startsWith("-")) {
                try {
                    MessageHandler.HandleMessageEvent(messageEvent);
                } catch (Exception e) {
                    messageEvent.getSubject().sendMessage(e.getMessage());
                }
            }
        });
    }
}