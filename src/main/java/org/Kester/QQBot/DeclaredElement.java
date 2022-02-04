package org.Kester.QQBot;

import org.Kester.QQBot.Games.Game;
import org.Kester.QQBot.Games.TXWZGame;
import org.Kester.QQBot.Games.YTRQGame;
import org.Kester.QQBot.Games.ZDQGame;

import java.util.Locale;

public enum DeclaredElement {

    YTRQ("-犹太人棋", "-ytrq", YTRQGame.class, "  犹太人棋游戏可供2人游戏\n" +
            "  双方轮流给一个6*6的棋盘涂黑色，\n" +
            "  一次可以只涂一格，也可以多格，已涂色的格子不能再涂，如果多格则必须是相连的横竖斜一条线上。谁涂完最后一格谁赢。\n",
            "B6、A2C4 -> 在对应位置涂色，字母在前，大小写均可\n"),

    ZDQ("-指定棋", "-zdq", ZDQGame.class, "每人6颗棋子，放在自己底线上，每回合移动一颗己方棋子之后，\n" +
            "指定对方一颗棋子，下回合对方只能移动被指定的棋子。\n" +
            "移动方式：竖向或斜向前进任意步数，不能横向或后退，不能越子。\n" +
            "被指定的棋子必须有能移动的空间，否则不能被指定。\n" +
            "胜利条件：一方一颗棋子到达对方底线直接获胜。当移动后无法指定对方任何棋子则直接失败。\n",
            "3 -> 初始回合指定第一个走的棋子",
            "B6 3 -> 走到B6，并且指定对方下个棋子为3"),

    TXWZ("-天下无贼", "-txwz", TXWZGame.class, "  每个人有10点生命，初始身份为民。\n" +
            "  总共有民、警、贼三种身份，每轮可以选择维持不变或变成别的身份，但是贼不能直接变警，警也不能直接变贼。\n" +
            "  选择警的人，该回合必须选择一个要抓的人，如果该人是贼，贼淘汰，自己+1生命，如果该人不是贼，自己-2生命。\n" +
            "  选择贼的人，该回合必须要选择偷一个人，如果该人是警，自己淘汰，如果该人不是警，该人-2生命，自己+1生命。\n" +
            "  选择民的不需要操作，如果场上没有警，所有民额外-1生命。\n" +
            "  当游戏进行到只有两人时，生命值多的夺冠。如果两人同分，共同获胜。\n" +
            "  补充提示：每轮结束都公布大家的身份和操作，所以如果某人前一轮是贼的话，这一轮绝对不会变成警。\n\n",
            "民 -> 成为平民",
            "警/贼 [操作对象] -> 成为警/贼，并对操作对象进行操作")
//    CZTB(CZTBGame.class, "游戏源于或改编于“45-差值法则”。\n" //
//            + "双方初始每人30枚点数，每回合同时下分一定数量\n" //
//            + "下分高的一方得1分，并支付双方本回合下分数额之差的点数（不给对方）\n" //
//            + "9回合后得分高的玩家获胜（平局加3场，再平则比剩余点数）", "[数字] -> 提交本轮数字（不输入中括号）"),
//
//    HYB2(HYB2Game.class, "双方玩家需将99点数分配至9个回合\n" //
//            + "每回合下分点数较高的玩家获得1分，先赢得5分的玩家获胜。\n" //
//            + "下分有先后顺序，该回合获胜的玩家将成为下回合的先手。\n" //
//            + "当下分的点数为1位数称为“黑”，2位数称为“白”；\n" //
//            + "此外，两位玩家初始拥有5盏灯，每当失去20点数灭1盏灯\n" //
//            + "5灯分别表示：0~19、20~39、40~59、60~79、80~99\n" //
//            + "（最终平局则比剩余点数，点数同则R1先手胜）", "[数字] -> 提交本轮数字（不输入中括号）"),
//
//    MRTP(MRTPGame.class, "所有玩家初始100生命，每回合提交一个数字（0~100）\n" //
//            + "之后计算x=所有玩家的平均数*80%\n" //
//            + "最接近x的人本轮优胜，其他人减10血，生命为0出局。\n" //
//            + "此外，追加如下额外规则（始终生效，与原作略有出入）：\n" //
//            + "【撞车】\n最接近的人数字相同时这些人扣血减半，从其他非撞车玩家选优胜\n" //
//            + "【命中红心】\n完美命中x的值（四舍五入）将造成双倍伤害：\n" //
//            + "【最终规则】\n若同时出现0和100，无视其他规则，本轮出0玩家出局：\n" //
//            + "\n", "[数字] -> 提交本轮数字（不输入中括号）"),
    ;

    DeclaredElement(String cn, String en, Class<? extends Game> clazz, String rule, String... COMMANDS) {
        this.CN = cn;
        this.EN = en;
        this.CLAZZ = clazz;
        this.RULE = rule;
        this.COMMANDS = COMMANDS;
    }

    public Class<? extends Game> CLAZZ;
    public String CN, EN, RULE;
    public String[] COMMANDS;

//    public static class CZTBGame extends Game {
//
//    }
//
//    public static class HYB2Game extends Game {
//
//    }
//
//    public static class MRTPGame extends Game {
//
//    }

    public static DeclaredElement findEnumByAllName(String msg) {
        for (DeclaredElement entity : DeclaredElement.values()) {
            if (msg.startsWith(entity.CN) || msg.toLowerCase().startsWith(entity.EN)) {
                return entity;
            }
        }
        return null;
    }

}
