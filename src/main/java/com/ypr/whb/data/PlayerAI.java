package com.ypr.whb.data;

import java.util.ArrayList;
import java.util.Random;

/**
 * 智能AI玩家
 * *
 * 修复轮流出棋，防止重复出相同颜色的棋子
 * *
 * 只是数据处理，生成最优出棋
 *
 * @author chaos
 */

public class PlayerAI extends Player {

    public PlayerAI() {
        this.name = "电脑AI";
    }

    /**
     * 创建将要下棋的最优对象，不会更改数据,仅返回棋子
     * *
     * 点击所产生的对象，加上参数color，以便棋色计算最优值
     */
    public Spot getBestChess(String mColor) {
        if (Spot.notChess.equals(mColor)) {
            System.out.println("playChess 颜色设置错误！");
            return null;
        }

        // 获得我方最大权值点
        Spot maxWeightSpot = maxSpot(mColor);

        // 如果未得到最大值，或者最大值的权值为零， 则随机下棋
        if (maxWeightSpot == null) {
            System.out.println("AI算法结果为空！随机下棋aaaa");
            return playRandom(mColor);
        }

        // 此段代码实现进攻
        // 同时计算对手的权值，获得权值最大的点
        String matchColor = Spot.getBackColor(mColor);// 得到对手的棋色
        Spot matchSpot = maxSpot(matchColor);

        int max_We = Algorithm.getWeight(maxWeightSpot, mColor);
        int max_They = Algorithm.getWeight(matchSpot, matchColor);
        System.out.println("我方最大值:" + max_We + ",  对方最大值:" + max_They);
        if (max_They - max_We > 550) {
            // 与对手的权值差是否超过100
            maxWeightSpot = matchSpot;
        }
        // 重新生成Spot对象，防止棋色错误
        int row = maxWeightSpot.getRow();
        int col = maxWeightSpot.getCol();

        System.out.println("AI落子为行：" +(row+1)+"列："+(col+1));

        return new Spot(row, col, mColor);
    }

    /**
     * 随机下棋，只产生对象，不会更改数据
     */
    public static Spot playRandom(String mColor) {
        Spot spot;
        int col;
        int row;
        while (true) {
            col = new Random().nextInt(19);
            row = new Random().nextInt(19);
            spot = TableData.getSpot(row, col);
            String color = spot.getColor();
            if (Spot.notChess.equals(color)) {
                break;
            }
        }
        return new Spot(row, col, mColor);
    }

    /**
     * 返回这个棋色的最大权值点
     */
    public static Spot maxSpot(String mColor) {
        if (Spot.notChess.equals(mColor)) {
            System.out.println("maxWeight 颜色设置错误！");
            return null;
        }
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Spot> listSpot = new ArrayList<>();

        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                Spot spot = TableData.getSpot(i, j);
                if (Spot.notChess.equals(spot.getColor())) {
                    int w = Algorithm.getWeight(spot, mColor);
                    list.add(w);
                    listSpot.add(spot);
                }
            }
        }

        if (list.size() < 1) {
            return null;
        }

        int max = 0;

        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(max) < list.get(i + 1)) {
                max = i + 1;
            }
        }

        return listSpot.get(max);
    }
}
