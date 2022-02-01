package org.Kester.QQBot.Games;


import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.Kester.QQBot.CONSTANTS.*;

public class YTRQGame extends Game {


    private int size = 6;
    private int[][][] board = new int[100][size][size];

    public YTRQGame() {
        super();
        name = "犹太人棋";
        minPlayer = 2;
        maxPlayer = 2;

        gridMarginTop = 40;
        gridMarginBottom = 40;
        gridMarginLeft = 40;
        gridMarginRight = 40;

        width = 80;
        height = 80;

        round = 0;

        waitTime = 600;
    }

    @Override
    protected void autoDo(int i) {

    }

    @Override
    protected void cal() {

    }

    @Override
    public void start() throws Exception {
        super.start();
        showInfo();
        round++;
        At at = new At(players.get(0).getId());
        MessageChain messages = at.plus(new PlainText("先手"));
        group.sendMessage(messages);
    }

    @Override
    public void doByEvent(MessageEvent messageEvent, String msg, long senderId, boolean isGroup) throws Exception {
        super.doByEvent(messageEvent, msg, senderId, isGroup);
        if (!isGroup) throw new Exception("请群聊");

        msg = msg.replace(getName(), "").trim().toLowerCase();
        if (msg.startsWith("back") || msg.startsWith("回退")) {
            msg = msg.replace("back", "").replace("回退", "").trim();
            int a;
            try {
                a = Integer.parseInt(msg);
            } catch (NumberFormatException e) {
                throw new Exception("回退到的回合大于等于当前回合");
            }
            if (a < 0) throw new Exception("回退到的回合小于0");
            if (a >= round) throw new Exception("回退到的回合大于等于当前回合");
            round = a;
            showInfo();
            round++;
            return;

        }
        if (senderId != players.get(1 - round % 2).getId())
            throw new Exception("不是你的回合");

        System.arraycopy(board[round - 1], 0, board[round], 0, size * size);

        if (msg.length() == 2) {
            char c0 = msg.charAt(0);
            char c1 = msg.charAt(1);
            if (c0 >= 'a' && c0 < 'a' + size && c1 >= '1' && c1 < '1' + size) {
                int i = c0 - 'a';
                int j = c1 - '1';
                if (board[round][i][j] == 1)
                    throw new Exception("格子已被占用");
                board[round][i][j] = 1;
                showInfo();
                round++;
                return;
            }
        } else if (msg.length() == 4) {
            char c0 = msg.charAt(0);
            char c1 = msg.charAt(1);
            char c2 = msg.charAt(2);
            char c3 = msg.charAt(3);
            if (c0 >= 'a' && c0 < 'a' + size && c1 >= '1' && c1 < '1' + size && c2 >= 'a' && c2 < 'a' + size && c3 >= '1' && c3 < '1' + size) {
                int i1 = c0 - 'a';
                int j1 = c1 - '1';
                int i2 = c2 - 'a';
                int j2 = c3 - '1';
                int di = Integer.compare(i2, i1);
                int dj = Integer.compare(j2, j1);
                if (di == 0 && dj == 0) throw new Exception("两组坐标相同");
                if (di == 0 || dj == 0 || Math.abs(i1 - i2) == Math.abs(j1 - j2)) {
                    for (int i = i1, j = j1; i != i2 + di || j != j2 + dj; i += di, j += dj) {
                        if (board[round][i][j] == 1) throw new Exception("格子已被占用");
                        board[round][i][j] = 1;
                    }
                    showInfo();
                    return;
                }
            }
        }
        throw new Exception("你的输入不符合规则");
    }

    @Override
    public void showInfo() throws IOException {
        group.sendMessage("R" + round);
        BufferedImage image = new BufferedImage(width * size + gridMarginLeft + gridMarginRight, height * size + gridMarginTop + gridMarginBottom, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        //画背景
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, width * size + gridMarginLeft + gridMarginRight, height * size + gridMarginTop + gridMarginBottom);

        graphics.setColor(textColor);
        graphics.setFont(indexFont);
        for (int i = 0; i < size; i++) {
            graphics.drawString(String.valueOf((char) ('1' + i)), gridMarginTop / 2, gridMarginLeft + height * i + width / 2 + 7);
            graphics.drawString(String.valueOf((char) ('A' + i)), gridMarginTop + width * i + width / 2 - 10, gridMarginLeft / 2 + 10);
        }
        //画格子
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (board[round][i][j] == 1) {
                    graphics.fillRect(gridMarginLeft + i * width, gridMarginTop + j * width, width, height);
                } else {
                    graphics.drawRect(gridMarginLeft + i * width, gridMarginTop + j * width, width, height);
                }
            }

        File file = writeImageToFile(image, "TEMPS", name + group.getId() + ".png");

        ExternalResource externalResource = ExternalResource.create(file);
        Message message = group.uploadImage(externalResource);
        group.sendMessage(message);
        externalResource.close();
    }
}
