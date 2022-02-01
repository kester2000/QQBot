package org.Kester.QQBot.Games;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.Kester.QQBot.CONSTANTS;

import static java.lang.Thread.sleep;

public abstract class ImperfectInfoGame extends Game {

    protected int waitTime = 180;
    protected boolean[] vis = new boolean[9999];

    @Override
    public void start() throws Exception {
        super.start();

        for (int i = 0; i < players.size(); i++) {
            vis[i] = false;
        }
        new Thread(this::loop).start();
    }

    protected void loop() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (this.state == CONSTANTS.STATE.PLAYING) {
            round++;
            int t = 0;
            while (this.state == CONSTANTS.STATE.PLAYING && t < waitTime) {
                t++;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean flag = true;
                for (int i = 0; i < players.size(); i++) {
                    if (!vis[i]) {
                        flag = false;
                        break;
                    }
                }
                if (flag) break;
                if (waitTime - t == 120 || waitTime - t == 60 || waitTime - t == 30 || waitTime - t == 15) {
                    MessageChainBuilder builder = new MessageChainBuilder();
                    for (int i = 0; i < players.size(); i++) {
                        if (!vis[i]) {
                            At at = new At(players.get(i).getId());
                            builder.add(at);
                        }
                    }
                    builder.add("仍未发送消息，" + (waitTime - t) + "秒后自动死亡");
                    MessageChain messages = builder.build();
                    group.sendMessage(messages);
                }
            }
            for (int i = 1; i <= players.size(); i++) {
                if (!vis[i]) autoDo(i);
            }
            cal();
        }
    }

    protected abstract void autoDo(int i);

    protected abstract void cal();

    @Override
    public void doByEvent(MessageEvent messageEvent, String msg, long senderId, boolean isGroup) throws Exception {
        super.doByEvent(messageEvent, msg, senderId, isGroup);
        if (isGroup) throw new Exception("请私聊");
    }
}
