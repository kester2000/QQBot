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

public class ZDQGame extends Game {


    private int size = 6;
    private int[][][] board = new int[100][size][size];

    private int nxt = -1;

    private class ZDQPlayer extends Player {
        private String[] pieces;

        public String getPiece(int i) {
            return pieces[i];
        }

        public String[] getPieces() {
            return pieces;
        }

        public void setPieces(int index, String s) throws Exception {
            if (index < 0 || index >= size) throw new Exception("代码bug,设置棋子坐标错误");
            pieces[index] = s;
        }

        public ZDQPlayer(long id, String nickName) throws Exception {
            super(id, nickName);
            pieces = new String[size];
        }
    }

    @Override
    public Player createPlayer(long id, String nickName) throws Exception {
        return new ZDQPlayer(id, nickName);
    }

    public ZDQGame() {
        super();
        name = "指定棋";
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
        ZDQPlayer zdqPlayer0 = (ZDQPlayer) players.get(0);
        for (int i = 0; i < size; i++) zdqPlayer0.setPieces(i, (char) ('a' + i) + String.valueOf(size));
        ZDQPlayer zdqPlayer1 = (ZDQPlayer) players.get(1);
        for (int i = 0; i < size; i++) zdqPlayer1.setPieces(i, (char) ('a' + size - i - 1) + "1");
        for (int i = 0; i < size; i++) board[0][i][size - 1] = i + 1;
        for (int i = 0; i < size; i++) board[0][size - i - 1][0] = i + 7;
        round = 0;
        showInfo();
        round++;
        At at = new At(players.get(0).getId());
        MessageChain messages = at.plus("先手，你为下方红色棋子，请先用\"-指定棋 [数字]\"自己要走的棋，再发送下一个指令");
        group.sendMessage(messages);
    }

    private boolean inside(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    @Override
    public void doByEvent(MessageEvent messageEvent, String msg, long senderId, boolean isGroup) throws Exception {
        super.doByEvent(messageEvent, msg, senderId, isGroup);
        if (!isGroup) throw new Exception("请群聊");

        msg = msg.replace(getName(), "").trim();
//        if (msg.startsWith("back") || msg.startsWith("回退")) {
//            msg = msg.replace("back", "").replace("回退", "").trim();
//            int a;
//            try {
//                a = Integer.parseInt(msg);
//            } catch (NumberFormatException e) {
//                throw new Exception("回退到的回合大于等于当前回合");
//            }
//            if (a < 0) throw new Exception("回退到的回合小于0");
//            if (a >= round) throw new Exception("回退到的回合大于等于当前回合");
//            round = a;
//            if (round == 0) nxt = -1;
//            showInfo();
//            round++;
//            return;
//        }

        if (senderId != players.get(1 - round % 2).getId())
            throw new Exception("不是你的回合");

        if (nxt == -1) {
            int a;
            try {
                a = Integer.parseInt(msg);
            } catch (NumberFormatException e) {
                throw new Exception("请先输入第一回合指定的棋子");
            }
            if (a < 1) throw new Exception("指定棋子不能小于1");
            if (a > size) throw new Exception("指定棋子不能大于" + size);
            nxt = a;
            group.sendMessage("指定" + nxt + "成功");
            return;
        }

        for (int i = 0; i < size; i++)
            System.arraycopy(board[round - 1][i], 0, board[round][i], 0, size);

        msg = msg.toLowerCase();
        String[] strings = msg.split("\\s+");
        if (strings.length < 2) throw new Exception("未知命令");
        String to = strings[0];
        String nextPiece = strings[1];
        if (to.length() != 2) throw new Exception("坐标出错");
        char c0 = to.charAt(0);
        char c1 = to.charAt(1);
        if (c0 < 'a' || c0 >= 'a' + size || c1 < '1' || c1 >= '1' + size) throw new Exception("坐标范围出错");
        ZDQPlayer zdqPlayer = (ZDQPlayer) players.get(1 - round % 2);
        String from = zdqPlayer.getPiece(nxt - 1);
        int i1 = from.charAt(0) - 'a';
        int j1 = from.charAt(1) - '1';
        int i2 = to.charAt(0) - 'a';
        int j2 = to.charAt(1) - '1';
        int di = Integer.compare(i2, i1);
        int dj = Integer.compare(j2, j1);
        if (1 - round % 2 == 0 && dj >= 0) throw new Exception("坐标方向出错");
        if (1 - round % 2 == 1 && dj <= 0) throw new Exception("坐标方向出错");
        if (di != 0 && Math.abs(i1 - i2) != Math.abs(j1 - j2)) throw new Exception("无法走到该位置");
        for (int i = i1 + di, j = j1 + dj; i != i2 + di || j != j2 + dj; i += di, j += dj) {
            if (board[round][i][j] != 0) throw new Exception("路径上有棋子");
        }
        board[round][i2][j2] = board[round][i1][j1];
        board[round][i1][j1] = 0;
        int a;
        try {
            a = Integer.parseInt(nextPiece);
        } catch (NumberFormatException e) {
            throw new Exception("请先输入指定对方棋子");
        }
        if (a < 1) throw new Exception("指定棋子不能小于1");
        if (a > size) throw new Exception("指定棋子不能大于" + size);

        ZDQPlayer zdqPlayer1 = (ZDQPlayer) players.get(round % 2);
        String nxtPos = zdqPlayer1.getPiece(a - 1);
        int i3 = nxtPos.charAt(0) - 'a';
        int j3 = nxtPos.charAt(1) - '1';
        int j4 = (1 - round % 2 == 0) ? 1 : -1;
        if ((!inside(i3 - 1, j3 + j4) || board[round][i3 - 1][j3 + j4] != 0)
                && (!inside(i3, j3 + j4) || board[round][i3][j3 + j4] != 0)
                && (!inside(i3 + 1, j3 + j4) || board[round][i3 + 1][j3 + j4] != 0))
            throw new Exception("被指定棋无法移动");
        zdqPlayer.setPieces(nxt - 1, to);
        nxt = a;
        showInfo();
        if ((1 - round % 2 == 0 && j2 == 0) || (1 - round % 2 == 1 && j2 == size - 1)) {
            group.sendMessage("游戏结束");
            kill();
        }
        round++;
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
        Color red = new Color(255, 0, 0);
        Color blue = new Color(0, 210, 240);
        graphics.setFont(bigFont);
        //画格子
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                graphics.drawRect(gridMarginLeft + i * width, gridMarginTop + j * height, width, height);
                if (board[round][i][j] >= 1 && board[round][i][j] <= 6) {
                    graphics.setColor(red);
                    graphics.fillRect(gridMarginLeft + i * width, gridMarginTop + j * height, width, height);
                    graphics.setColor(textColor);
                    graphics.drawString(String.valueOf(board[round][i][j]), gridMarginLeft + i * width + width / 2, gridMarginTop + j * height + height / 2 + graphics.getFont().getSize() / 2);
                } else if (board[round][i][j] >= 7 && board[round][i][j] <= 12) {
                    graphics.setColor(blue);
                    graphics.fillRect(gridMarginLeft + i * width, gridMarginTop + j * width, width, height);
                    graphics.setColor(textColor);
                    graphics.drawString(String.valueOf(board[round][i][j] - 6), gridMarginLeft + i * width + width / 2, gridMarginTop + j * height + height / 2 + graphics.getFont().getSize() / 2);
                }
            }

        File file = writeImageToFile(image, "TEMPS", name + group.getId() + ".png");

        ExternalResource externalResource = ExternalResource.create(file);
        Message message = group.uploadImage(externalResource);
        group.sendMessage(message);
        externalResource.close();
    }
}
