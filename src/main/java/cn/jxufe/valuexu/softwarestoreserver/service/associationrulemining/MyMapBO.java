package cn.jxufe.valuexu.softwarestoreserver.service.associationrulemining;

import lombok.Getter;

import java.util.List;

/**
 * 自定义的map类，一个对象存放一个频繁项集以及其支持度计数
 */
class MyMapBO {
    /**
     * 频繁项集
     */
    private final List<String> li;

    /**
     * 项集的支持度计数
     * -- GETTER --
     *  返回得到当前频繁项集的支持度计数

     */
    @Getter
    private final int count;

    /**
     * 构造函数 新建一个对象
     */
    public MyMapBO(List<String> l, int c) {
        li = l;
        count = c;
    }

    /**
     * 判断传入的频繁项集是否和本频繁项集相同
     */
    public boolean isListEqual(List<String> in) {
        // todo 判空
        //先判断大小是否相同
        if (in.size() != li.size()) {
            return false;
        }

        //遍历输入的频繁项集，判断是否所有元素都包含在本频繁项集中
        for (String s : in) {
            if (!li.contains(s))
                return false;
        }
        //如果两个频繁项集大小相同，同时本频繁项集包含传入的频繁项集的所有元素，则表示两个频繁项集是相等的，返回为真
        return true;
    }
}
