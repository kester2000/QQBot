package org.Kester.QQBot;

import org.Kester.QQBot.Games.Game;
import org.Kester.QQBot.Games.TXWZGame;

public enum DeclaredElement {

    TXWZ("-天下无贼", TXWZGame.class, "  每个人有10点生命，初始身份为民。\n" +
            "  总共有民、警、贼三种身份，每轮可以选择维持不变或变成别的身份，但是贼不能直接变警，警也不能直接变贼。\n" +
            "  选择警的人，该回合必须选择一个要抓的人，如果该人是贼，贼淘汰，自己+1生命，如果该人不是贼，自己-2生命。\n" +
            "  选择贼的人，该回合必须要选择偷一个人，如果该人是警，自己淘汰，如果该人不是警，该人-2生命，自己+1生命。\n" +
            "  选择民的不需要操作，如果场上没有警，所有民额外-1生命。\n" +
            "  当游戏进行到只有两人时，生命值多的夺冠。如果两人同分，共同获胜。\n" +
            "  补充提示：每轮结束都公布大家的身份和操作，所以如果某人前一轮是贼的话，这一轮绝对不会变成警。\n\n",
            "-天下无贼 民 -> 成为平民",
            "-天下无贼 警/贼 [操作对象] -> 成为警/贼，并对操作对象进行操作")
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

    DeclaredElement(String cn, Class<? extends Game> clazz, String rule, String... COMMANDS) {
        this.CN = cn;
        this.CLAZZ = clazz;
        this.RULE = rule;
        this.COMMANDS = COMMANDS;
    }

    public Class<? extends Game> CLAZZ;
    public String CN, RULE;
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

    public static DeclaredElement findEnumByAllName(String name) {
        for (DeclaredElement entity : DeclaredElement.values()) {
            if (entity.CN.equalsIgnoreCase(name)) {
                return entity;
            }
        }
        return null;
    }

}
