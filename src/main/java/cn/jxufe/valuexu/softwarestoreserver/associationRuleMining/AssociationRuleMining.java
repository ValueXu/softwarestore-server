package cn.jxufe.valuexu.softwarestoreserver.associationRuleMining;

import cn.jxufe.valuexu.softwarestoreserver.dao.AssociationRuleRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.RecordRepository;
import cn.jxufe.valuexu.softwarestoreserver.dao.UserRepository;
import cn.jxufe.valuexu.softwarestoreserver.domain.AssociationRule;
import cn.jxufe.valuexu.softwarestoreserver.domain.Record;
import cn.jxufe.valuexu.softwarestoreserver.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Component
public class AssociationRuleMining {

    private static Logger logger = LoggerFactory.getLogger(AssociationRuleMining.class);
    public static int times = 0;//迭代次数
    private static double MIN_SUPPORT = 0.02;//最小支持度百分比
    private static double MIN_CONFIDENCE = 0.6;//最小置信度
    private static boolean endTag = false;//循环状态，迭代标识


    //数据集
    static List<List<String>> record = new ArrayList<List<String>>();
    //存储所有的频繁项集
    static List<List<String>> frequentItems = new ArrayList<>();
    //存放频繁项集和对应的支持度计数
    static List<MyMap> map = new ArrayList();

    @Resource
    private AssociationRuleRepository associationRuleRepository;

    @Resource
    private RecordRepository recordRepository;

    @Resource
    private UserRepository userRepository;

    public static AssociationRuleMining current;

    @PostConstruct
    public void init() {
        current = this;
        current.recordRepository = this.recordRepository;
        current.userRepository = this.userRepository;
        current.associationRuleRepository = this.associationRuleRepository;
    }

    public static void start(Double support, Double confidence) {

        //定义最小支持度（如0.05）和最小置信度（如0.6）
        MIN_SUPPORT = support;
        MIN_CONFIDENCE = confidence;


        /*************读取数据集**************/
        record = getRecord();
        //控制台输出记录
        logger.info("读取数据集record成功===================================");
        ShowData(record);


        Apriori();//调用Apriori算法获得频繁项集
        logger.info("频繁模式挖掘完毕。");
        logger.info("进行关联度挖掘，最小支持度百分比为：" + MIN_SUPPORT + " 最小置信度为：" + MIN_CONFIDENCE);

        AssociationRulesMining();//挖掘关联规则
    }


    //   从数据库读取数据
    public static List<List<String>> getRecord() {
        List<List<String>> record = new ArrayList<List<String>>();
        try {
            List<User> userList = current.userRepository.findAll();
            for (int i = 0; i < userList.size(); i++) {
                User item = userList.get(i);
                String username = item.getUsername();
                List<Record> recordList = current.recordRepository.findAllByUserName(username);
                List<String> list = new ArrayList<>();
                for (int j = 0; j < recordList.size(); j++) {
                    list.add(Long.toString(recordList.get(j).getSoftwareId()));
                }
                if (recordList.size() > 0) {
                    // 只加入有下载记录的用户的记录进去
                    record.add(list);
                }
            }
        } catch (Exception e) {
            logger.info("读取数据库数据出错" + e.getMessage());
            e.printStackTrace();
        }
        return record;
    }

    //实现apriori算法
    public static void Apriori() {
        //************获取候选1项集**************
        logger.info("第一次扫描后的1级 备选集firstCandidate");
        List<List<String>> firstCandidate = findFirstCandidate();
        ShowData(firstCandidate);


        //************获取频繁1项集***************
        logger.info("第一次扫描后的1级 频繁集frequentItems");
        List<List<String>> frequentItems = getSupportedItems(firstCandidate);
        AddToFrequenceItem(frequentItems);//添加到所有的频繁项集中
        //控制台输出1项频繁集
        ShowData(frequentItems);


        //*****************************迭代过程**********************************
        times = 2;
        while (endTag != true) {

            logger.info("*******************************第" + times + "次扫描后备选集");
            //**********连接操作****获取候选times项集**************
            List<List<String>> nextCandidateItems = getNextCandidate(frequentItems);
            //输出所有的候选项集
            ShowData(nextCandidateItems);


            /**************计数操作***由候选k项集选择出频繁k项集****************/
            logger.info("*******************************第" + times + "次扫描后频繁集");
            List<List<String>> nextFrequentItemset = getSupportedItems(nextCandidateItems);
            AddToFrequenceItem(nextFrequentItemset);//添加到所有的频繁项集中
            //输出所有的频繁项集
            ShowData(nextFrequentItemset);


            //*********如果循环结束，输出最大模式**************
            if (endTag == true) {
                logger.info("Apriori算法--->最大频繁集==================================");
                ShowData(frequentItems);
            }
            //****************下一次循环初值********************
            frequentItems = nextFrequentItemset;
            times++;//迭代次数加一
        }
    }

    //关联规则挖掘
    public static void AssociationRulesMining() {
        for (int i = 0; i < frequentItems.size(); i++) {
            List<String> tem = frequentItems.get(i);
            if (tem.size() > 1) {
                List<String> temclone = new ArrayList<>(tem);
                List<List<String>> AllSubset = getSubSet(temclone);//得到频繁项集tem的所有子集
                for (int j = 0; j < AllSubset.size(); j++) {
                    List<String> s1 = AllSubset.get(j);
                    List<String> s2 = gets2set(tem, s1);
                    double conf = isAssociationRules(s1, s2, tem);
                    if (conf > 0)
                        logger.info("置信度为：" + conf);
                }
            }

        }
    }

    //显示出candidateitem中的所有的项集
    public static void ShowData(List<List<String>> candidateItemset) {
        for (int i = 0; i < candidateItemset.size(); i++) {
            List<String> list = new ArrayList<String>(candidateItemset.get(i));
            for (int j = 0; j < list.size(); j++) {
                logger.info(list.get(j) + " ");
            }
        }
    }

    //获得一项候选集
    private static List<List<String>> findFirstCandidate() {
        // TODO Auto-generated method stub
        List<List<String>> tableList = new ArrayList<List<String>>();
        HashSet<String> hs = new HashSet<String>();//新建一个hash表，存放所有的不同的一维数据

        for (int i = 1; i < record.size(); i++) { //遍历所有的数据集，找出所有的不同的软件存放到hs中
            for (int j = 1; j < record.get(i).size(); j++) {
                hs.add(record.get(i).get(j));
            }
        }
        Iterator<String> itr = hs.iterator();
        while (itr.hasNext()) {
            List<String> tempList = new ArrayList<String>();
            String Item = (String) itr.next();
            tempList.add(Item); //将每一种软件存放到一个List<String>中
            tableList.add(tempList);//所有的list<String>存放到一个大的list中
        }
        return tableList;//返回所有的软件
    }

    /**
     * 由k项候选集剪枝得到k项频繁集
     */
    private static List<List<String>> getSupportedItems(List<List<String>> CandidateItems) {
        // 对所有的软件进行支持度计数
        boolean end = true;
        List<List<String>> supportedItems = new ArrayList<List<String>>();

        for (int i = 0; i < CandidateItems.size(); i++) {

            int count = countFrequent1(CandidateItems.get(i));//统计记录数

            if (count >= MIN_SUPPORT * (record.size() - 1)) {
                supportedItems.add(CandidateItems.get(i));
                map.add(new MyMap(CandidateItems.get(i), count));//存储当前频繁项集以及它的支持度计数
                end = false;
            }
        }
        endTag = end;//存在频繁项集则不会结束
        if (endTag == true)
            logger.info("*****************无满足支持度的" + times + "项集,结束连接");
        return supportedItems;
    }


    //有当前频繁项集自连接求下一次候选集
    private static List<List<String>> getNextCandidate(List<List<String>> FrequentItemset) {
        List<List<String>> nextCandidateItemset = new ArrayList<List<String>>();

        for (int i = 0; i < FrequentItemset.size(); i++) {
            HashSet<String> hsSet = new HashSet<String>();
            HashSet<String> hsSettemp = new HashSet<String>();
            for (int k = 0; k < FrequentItemset.get(i).size(); k++)//获得频繁集第i行
                hsSet.add(FrequentItemset.get(i).get(k));
            int hsLength_before = hsSet.size();//添加前长度
            hsSettemp = (HashSet<String>) hsSet.clone();
            for (int h = i + 1; h < FrequentItemset.size(); h++) {//频繁集第i行与第j行(j>i)连接 每次添加且添加一个元素组成 新的频繁项集的某一行，
                hsSet = (HashSet<String>) hsSettemp.clone();//！！！做连接的hasSet保持不变
                for (int j = 0; j < FrequentItemset.get(h).size(); j++)
                    hsSet.add(FrequentItemset.get(h).get(j));
                int hsLength_after = hsSet.size();
                if (hsLength_before + 1 == hsLength_after && isNotHave(hsSet, nextCandidateItemset)) {
                    //如果不相等，表示添加了1个新的元素 同时判断其不是候选集中已经存在的一项
                    Iterator<String> itr = hsSet.iterator();
                    List<String> tempList = new ArrayList<String>();
                    while (itr.hasNext()) {
                        String Item = (String) itr.next();
                        tempList.add(Item);
                    }
                    nextCandidateItemset.add(tempList);
                }

            }

        }
        return nextCandidateItemset;
    }


    //判断是否为关联规则
    public static double isAssociationRules(List<String> s1, List<String> s2, List<String> tem) {
        double confidence = 0;
        int counts1;
        int countTem;
        if (s1.size() != 0 && s1 != null && tem.size() != 0 && tem != null) {
            counts1 = getCount(s1);
            countTem = getCount(tem);
            confidence = countTem * 1.0 / counts1;

            if (confidence >= MIN_CONFIDENCE) {
                logger.info("关联规则：" + s1.toString() + "=>>" + s2.toString() + " ");
                // 存入数据库中
                if (s1.size() == 1) {
                    AssociationRule associationRule = new AssociationRule();
                    associationRule.setSoftwareId0(Long.parseLong(s1.get(0)));
                    for (int i = 0; i < s2.size(); i++) {
                        Long currentId = Long.parseLong(s2.get(i));
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
                    associationRule.setConfidence(MIN_CONFIDENCE);
                    associationRule.setSupport(MIN_SUPPORT);
                    current.associationRuleRepository.saveAndFlush(associationRule);
                }
                return confidence;
            } else
                return 0;

        } else
            return 0;

    }

    //根据频繁项集得到其支持度计数
    public static int getCount(List<String> in) {
        int rt = 0;
        for (int i = 0; i < map.size(); i++) {
            MyMap tem = map.get(i);
            if (tem.isListEqual(in)) {
                rt = tem.getCount();
                return rt;
            }
        }
        return rt;

    }

    //计算tem减去s1后的集合即为s2
    public static List<String> gets2set(List<String> tem, List<String> s1) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < tem.size(); i++)//去掉s1中的所有元素
        {
            String t = tem.get(i);
            if (!s1.contains(t))
                result.add(t);
        }
        return result;
    }


    public static List<List<String>> getSubSet(List<String> set) {
        List<List<String>> result = new ArrayList<>(); //用来存放子集的集合，如{{},{1},{2},{1,2}}
        int length = set.size();
        int num = length == 0 ? 0 : 1 << (length); //2的n次方，若集合set为空，num为0；若集合set有4个元素，那么num为16.

        //从0到2^n-1（[00...00]到[11...11]）
        for (int i = 1; i < num - 1; i++) {
            List<String> subSet = new ArrayList<>();

            int index = i;
            for (int j = 0; j < length; j++) {
                if ((index & 1) == 1) { //每次判断index最低位是否为1，为1则把集合set的第j个元素放到子集中
                    subSet.add(set.get(j));
                }
                index >>= 1; //右移一位
            }

            result.add(subSet); //把子集存储起来
        }
        return result;
    }


    public static boolean AddToFrequenceItem(List<List<String>> fre) {

        for (int i = 0; i < fre.size(); i++) {
            frequentItems.add(fre.get(i));
        }
        return true;
    }


    // 判断新添加元素形成的候选集是否在新的候选集中
    private static boolean isNotHave(HashSet<String> hsSet, List<List<String>> nextCandidateItemset) {
        //判断hsset是不是candidateitemset中的一项
        List<String> tempList = new ArrayList<String>();
        Iterator<String> itr = hsSet.iterator();
        while (itr.hasNext()) {
            //将hsset转换为List<String>
            String Item = (String) itr.next();
            tempList.add(Item);
        }
        //遍历candidateitemset，看其中是否有和templist相同的一项
        for (int i = 0; i < nextCandidateItemset.size(); i++)
            if (tempList.equals(nextCandidateItemset.get(i)))
                return false;
        return true;
    }


    /**
     * 统计record中出现list集合的个数
     */
    private static int countFrequent1(List<String> list) {//遍历所有数据集record，对单个候选集进行支持度计数

        int count = 0;
        for (int i = 0; i < record.size(); i++)//从record的第一个开始遍历
        {
            boolean flag = true;
            for (int j = 0; j < list.size(); j++)//如果record中的第一个数据集包含list中的所有元素
            {
                String t = list.get(j);
                if (!record.get(i).contains(t)) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                count++;//支持度加一
        }

        return count;//返回支持度计数

    }


}

