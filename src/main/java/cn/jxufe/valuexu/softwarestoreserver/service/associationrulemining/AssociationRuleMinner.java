package cn.jxufe.valuexu.softwarestoreserver.service.associationrulemining;

import cn.jxufe.valuexu.softwarestoreserver.dao.AssociationRuleRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.AssociationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AssociationRuleMinner {
    private static final Logger logger = LoggerFactory.getLogger(AssociationRuleMinner.class);
    private static final int DEFAULT_TIMES = 0;
    private static final double DEFAULT_MIN_SUPPORT = 0.02;
    private static final double DEFAULT_MIN_CONFIDENCE = 0.6;
    /**
     * 迭代次数
     */
    private int times = DEFAULT_TIMES;
    /**
     * 最小支持度百分比
     */
    private double minSupport = DEFAULT_MIN_SUPPORT;
    /**
     * 最小置信度
     */
    private double minConfidence = DEFAULT_MIN_CONFIDENCE;
    /**
     * 循环状态，迭代标识
     */
    private boolean endTag = false;


    /**
     * 数据集
     */
    private List<List<String>> records = new ArrayList<>();
    //存储所有的频繁项集
    private final List<List<String>> frequentItems = new ArrayList<>();
    //存放频繁项集和对应的支持度计数
    private final List<MyMapBO> map = new ArrayList<>();

    private final AssociationRuleRepository associationRuleRepository;


    private AssociationRuleMinner(List<List<String>> records, AssociationRuleRepository associationRuleRepository) {
        this.records = records;
        this.associationRuleRepository = associationRuleRepository;
    }

    private AssociationRuleMinner(double minSupport, double minConfidence, int times, List<List<String>> records, AssociationRuleRepository associationRuleRepository) {
        this.records = records;
        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
        this.times = times;
        this.associationRuleRepository = associationRuleRepository;
    }

    public void start() {

        /************读取数据集**************/
        // 控制台输出记录
        logger.info("读取数据集record成功===================================");
        showData(records);

        // 调用Apriori算法获得频繁项集
        apriori();
        logger.info("频繁模式挖掘完毕。");
        logger.info("进行关联度挖掘，最小支持度百分比为：{} 最小置信度为：{}", minSupport, minConfidence);

        //挖掘关联规则
        associationRulesMining();
    }


    /**
     * 实现apriori算法
     */
    public void apriori() {
        //************获取候选1项集**************
        logger.info("第一次扫描后的1级 备选集firstCandidate");
        List<List<String>> firstCandidate = findFirstCandidate();
        showData(firstCandidate);


        //************获取频繁1项集***************
        logger.info("第一次扫描后的1级 频繁集frequentItems");
        List<List<String>> frequentItems = getSupportedItems(firstCandidate);
        addToFrequenceItem(frequentItems);//添加到所有的频繁项集中
        //控制台输出1项频繁集
        showData(frequentItems);


        //*****************************迭代过程**********************************
        times = 2;
        while (!endTag) {

            logger.info("*******************************第{}次扫描后备选集", times);
            //**********连接操作****获取候选times项集**************
            List<List<String>> nextCandidateItems = getNextCandidate(frequentItems);
            //输出所有的候选项集
            showData(nextCandidateItems);


            /**************计数操作***由候选k项集选择出频繁k项集****************/
            logger.info("*******************************第" + times + "次扫描后频繁集");
            List<List<String>> nextFrequentItemset = getSupportedItems(nextCandidateItems);
            addToFrequenceItem(nextFrequentItemset);//添加到所有的频繁项集中
            //输出所有的频繁项集
            showData(nextFrequentItemset);


            //*********如果循环结束，输出最大模式**************
            if (endTag) {
                logger.info("Apriori算法--->最大频繁集==================================");
                showData(frequentItems);
            }
            //****************下一次循环初值********************
            frequentItems = nextFrequentItemset;
            times++;//迭代次数加一
        }
    }

    //关联规则挖掘
    public void associationRulesMining() {
        for (List<String> frequentItem : frequentItems) {
            if (frequentItem.isEmpty()) {
                return;
            }
            List<String> temclone = new ArrayList<>(frequentItem);
            // 得到频繁项集tem的所有子集
            List<List<String>> allSubset = getSubSet(temclone);
            for (List<String> s1 : allSubset) {
                List<String> s2 = gets2set(frequentItem, s1);
                double conf = isAssociationRules(s1, s2, frequentItem);
                if (conf > 0) {
                    logger.info("置信度为：{}", conf);
                }
            }
        }
    }

    // 显示出candidateitem中的所有的项集
    public void showData(List<List<String>> candidateItemset) {
        for (List<String> strings : candidateItemset) {
            List<String> list = new ArrayList<>(strings);
            for (String s : list) {
                logger.info("{} ", s);
            }
        }
    }

    //获得一项候选集
    private List<List<String>> findFirstCandidate() {
        // TODO Auto-generated method stub
        List<List<String>> tableList = new ArrayList<>();
        //新建一个hash表，存放所有的不同的一维数据
        HashSet<String> hs = new HashSet<>();
        //遍历所有的数据集，找出所有的不同的软件存放到hs中
        for (List<String> record : records) {
            hs.addAll(record);
        }
//        for (int i = 1; i < records.size(); i++) {
//            for (int j = 1; j < records.get(i).size(); j++) {
//                hs.add(records.get(i).get(j));
//            }
//        }
        for (String h : hs) {
            List<String> tempList = new ArrayList<>();
            // 将每一种软件存放到一个List<String>中
            tempList.add(h);
            // 所有的list<String>存放到一个大的list中
            tableList.add(tempList);
        }
        // 返回所有的软件
        return tableList;
    }

    /**
     * 由k项候选集剪枝得到k项频繁集
     */
    private List<List<String>> getSupportedItems(List<List<String>> candidateItems) {
        // 对所有的软件进行支持度计数
        boolean end = true;
        List<List<String>> supportedItems = new ArrayList<>();

        for (List<String> candidateItem : candidateItems) {
            //统计记录数
            int count = countFrequent1(candidateItem);

            if (count >= minSupport * (records.size() - 1)) {
                supportedItems.add(candidateItem);
                //存储当前频繁项集以及它的支持度计数
                map.add(new MyMapBO(candidateItem, count));
                end = false;
            }
        }
        //存在频繁项集则不会结束
        endTag = end;
        if (endTag) {
            logger.info("*****************无满足支持度的{}项集,结束连接", times);
        }
        return supportedItems;
    }


    //有当前频繁项集自连接求下一次候选集
    private List<List<String>> getNextCandidate(List<List<String>> frequentItemset) {
        List<List<String>> nextCandidateItemset = new ArrayList<>();

        for (int i = 0; i < frequentItemset.size(); i++) {
            HashSet<String> hsSettemp = new HashSet<>();
            //获得频繁集第i行
            HashSet<String> hsSet = new HashSet<>(frequentItemset.get(i));
            int hsLength_before = hsSet.size();//添加前长度
            hsSettemp = (HashSet<String>) hsSet.clone();
            for (int h = i + 1; h < frequentItemset.size(); h++) {//频繁集第i行与第j行(j>i)连接 每次添加且添加一个元素组成 新的频繁项集的某一行，
                hsSet = (HashSet<String>) hsSettemp.clone();//！！！做连接的hasSet保持不变
                hsSet.addAll(frequentItemset.get(h));
                int hsLength_after = hsSet.size();
                if (hsLength_before + 1 == hsLength_after && isNotHave(hsSet, nextCandidateItemset)) {
                    //如果不相等，表示添加了1个新的元素 同时判断其不是候选集中已经存在的一项
                    Iterator<String> itr = hsSet.iterator();
                    List<String> tempList = new ArrayList<>();
                    while (itr.hasNext()) {
                        String item = itr.next();
                        tempList.add(item);
                    }
                    nextCandidateItemset.add(tempList);
                }

            }

        }
        return nextCandidateItemset;
    }


    //判断是否为关联规则
    public double isAssociationRules(List<String> s1, List<String> s2, List<String> tem) {
        double confidence = 0;
        int counts1;
        int countTem;
        if (s1 == null || s1.isEmpty() || s2 == null || s2.isEmpty()) {
            return 0;
        }
        counts1 = getCount(s1);
        countTem = getCount(tem);
        confidence = countTem * 1.0 / counts1;

        if (confidence < minConfidence) {
            return 0;
        }

        logger.info("关联规则：{}=>>{}", s1, s2);
        // 存入数据库中
        if (s1.size() == 1) {
            AssociationRule associationRule = new AssociationRule();
            associationRule.setSoftwareId0(Long.parseLong(s1.get(0)));
            for (int i = 0; i < s2.size(); i++) {
                long currentId = Long.parseLong(s2.get(i));
                switch (i) {
                    case 0:
                        associationRule.setSoftwareId1(currentId);
                        break;
                    case 1:
                        associationRule.setSoftwareId2(currentId);
                        break;
                    case 2:
                        associationRule.setSoftwareId3(currentId);
                        break;
                    case 3:
                        associationRule.setSoftwareId4(currentId);
                        break;
                }
            }
            associationRule.setConfidence(minConfidence);
            associationRule.setSupport(minSupport);
            associationRuleRepository.saveAndFlush(associationRule);
        }
        return confidence;
    }

    //根据频繁项集得到其支持度计数
    public int getCount(List<String> in) {
        int rt = 0;
        for (MyMapBO tem : map) {
            if (tem.isListEqual(in)) {
                rt = tem.getCount();
                return rt;
            }
        }
        return rt;

    }

    //计算tem减去s1后的集合即为s2
    public List<String> gets2set(List<String> tem, List<String> s1) {
        List<String> result = new ArrayList<>();

        //去掉s1中的所有元素
        for (String t : tem) {
            if (!s1.contains(t))
                result.add(t);
        }
        return result;
    }


    public List<List<String>> getSubSet(List<String> set) {
        //用来存放子集的集合，如{{},{1},{2},{1,2}}
        List<List<String>> result = new ArrayList<>();
        int length = set.size();
        //2的n次方，若集合set为空，num为0；若集合set有4个元素，那么num为16.
        int num = length == 0 ? 0 : 1 << (length);

        //从0到2^n-1（[00...00]到[11...11]）
        for (int i = 1; i < num - 1; i++) {
            List<String> subSet = new ArrayList<>();

            int index = i;
            for (String s : set) {
                //每次判断index最低位是否为1，为1则把集合set的第j个元素放到子集中
                if ((index & 1) == 1) {
                    subSet.add(s);
                }
                index >>= 1; //右移一位
            }

            result.add(subSet); //把子集存储起来
        }
        return result;
    }


    public boolean addToFrequenceItem(List<List<String>> fre) {
        frequentItems.addAll(fre);
        return true;
    }


    // 判断新添加元素形成的候选集是否在新的候选集中
    private boolean isNotHave(HashSet<String> hsSet, List<List<String>> nextCandidateItemset) {
        //判断hsset是不是candidateitemset中的一项
        //将hsset转换为List<String>
        List<String> tempList = new ArrayList<>(hsSet);
        //遍历candidateitemset，看其中是否有和templist相同的一项
        for (List<String> strings : nextCandidateItemset) {
            if (tempList.equals(strings)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 统计record中出现list集合的个数
     */
    private int countFrequent1(List<String> list) {
        //遍历所有数据集record，对单个候选集进行支持度计数
        int count = 0;
        //从record的第一个开始遍历
        for (List<String> strings : records) {
            boolean flag = true;
            //如果record中的第一个数据集包含list中的所有元素
            for (String t : list) {
                if (!strings.contains(t)) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                count++;//支持度加一
        }

        return count;//返回支持度计数

    }

    public static class AssociationRuleMiningBuilder {
        private final List<List<String>> records;
        private final AssociationRuleRepository associationRuleRepository;
        /**
         * 迭代次数
         */
        private int times = DEFAULT_TIMES;
        /**
         * 最小支持度百分比
         */
        private double minSupport = DEFAULT_MIN_SUPPORT;
        /**
         * 最小置信度
         */
        private double minConfidence = DEFAULT_MIN_CONFIDENCE;

        AssociationRuleMiningBuilder(List<List<String>> records, AssociationRuleRepository associationRuleRepository) {
            this.records = records;
            this.associationRuleRepository = associationRuleRepository;
        }

        public AssociationRuleMiningBuilder times(int times) {
            this.times = times;
            return this;
        }

        public AssociationRuleMiningBuilder minSupport(double minSupport) {
            this.minSupport = minSupport;
            return this;
        }

        public AssociationRuleMiningBuilder minConfidence(double minConfidence) {
            this.minConfidence = minConfidence;
            return this;
        }

        public AssociationRuleMinner build() {
            return new AssociationRuleMinner(minSupport, minConfidence, times, records, associationRuleRepository);
        }
    }

    public static AssociationRuleMiningBuilder builder(List<List<String>> record, AssociationRuleRepository associationRuleRepository) {
        return new AssociationRuleMiningBuilder(record, associationRuleRepository);
    }
}

