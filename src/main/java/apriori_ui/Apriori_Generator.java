package apriori_ui;

import java.util.*;

public class Apriori_Generator<I> {

    public FrequentItemsetData<I> generate(List<Set<I>> transactionList, double minimumSupport){
        //Objects.requireNonNull(transactionList,"The itemset List is empty!");
        checkSupport(minimumSupport);

        if(transactionList.isEmpty()){
            throw new IllegalArgumentException("The itemset List is Empty");
        }

        Map<Set<I>,Integer> supportCountItems = new HashMap<>();

        List<Set<I>> frequent_1_ItemList = findFrequentItems(transactionList, supportCountItems, minimumSupport);

        Map<Integer, List<Set<I>>> Level_Items = new HashMap<>();  //maps each level to the list
        Level_Items.put(1, frequent_1_ItemList);

        int level = 1;

        do{
            ++level;
            List<Set<I>> candidateList = generateCandidates(Level_Items.get(level-1));

            for(Set<I> transaction: transactionList){
                List<Set<I>> candidateList2 = subset(candidateList, transaction);

                for(Set<I> itemset: candidateList2){
                    supportCountItems.put(itemset,supportCountItems.getOrDefault(itemset,0)+1);
                }
            }

            Level_Items.put(level, pruning(candidateList, supportCountItems, minimumSupport));

        } while(!Level_Items.get(level).isEmpty());

        return new FrequentItemsetData<>(extractSelected(Level_Items),supportCountItems,minimumSupport, transactionList.size());
    }

    private List<Set<I>> extractSelected(Map<Integer, List<Set<I>>> candi){
        List<Set<I>> selected = new ArrayList<>();

        for(List<Set<I>> itemsetList : candi.values()){
            selected.addAll(itemsetList);
        }
        return selected;
    }


    private List<Set<I>> pruning(List<Set<I>> candidateList, Map<Set<I>, Integer> supportCount, double min_sup){
        List<Set<I>> candi = new ArrayList<>(candidateList.size());

        for(Set<I> itemset: candidateList){
            if(supportCount.containsKey(itemset)){
                int sup_count = supportCount.get(itemset);
                double support = 1.0 * sup_count;

                if(support>=min_sup){
                    candi.add(itemset);
                }
            }
        }
        return candi;
    }


    private List<Set<I>> subset(List<Set<I>> candidateList, Set<I> transaction){
        List<Set<I>> candi = new ArrayList<>(candidateList.size());

        for(Set<I> candidate: candidateList){
            if(transaction.containsAll(candidate)){
                candi.add(candidate);
            }
        }

        return candi;
    }

    private List<Set<I>> generateCandidates(List<Set<I>> itemsetList){
        List<List<I>> list = new ArrayList<>(itemsetList.size());

        for(Set<I> itemset: itemsetList){
            List<I> l = new ArrayList<>(itemset);
            Collections.sort(l,item_compare);
            list.add(l);
        }

        int listSize = list.size();

        List<Set<I>> join = new ArrayList<>(listSize);

        for(int i=0;i<listSize; ++i){
            for(int j= i+1; j<listSize; ++j){
                Set<I> candidate = joinItemSets(list.get(i),list.get(j));
                if(candidate!= null){
                    join.add(candidate);
                }
            }
        }

        return join;
    }

    private Set<I> joinItemSets(List<I> itemSet1, List<I> itemSet2){
        int length = itemSet1.size();

        for(int i=0;i<length-1; ++i){
            if(!itemSet1.get(i).equals(itemSet2.get(i))){
                return null;
            }
        }

        if(itemSet1.get(length-1).equals((itemSet2.get(length-1)))){
            return null;
        }

        Set<I> prefixAdd = new HashSet<>(length+1);

        for(int i=0; i<length-1; ++i){
            prefixAdd.add(itemSet1.get(i));
        }

        prefixAdd.add(itemSet1.get(length-1));     // add 2 from {1,2},{1,3}
        prefixAdd.add(itemSet2.get(length-1));     // add 3 from {1,2}, {1,3}
        return prefixAdd;
    }

    private static final Comparator item_compare = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            return ((Comparable) o1).compareTo(o2);
        }
    };

    private List<Set<I>> findFrequentItems(List<Set<I>> itemsetList,
                                           Map<Set<I>,Integer> supportCountItems,
                                           double minimumSupport){
        Map<I,Integer> map = new HashMap<>();

        for(Set<I> itemset: itemsetList){
            for(I item: itemset){
                Set<I> tmp = new HashSet<>(1);
                tmp.add(item);
                if(supportCountItems.containsKey(tmp)){
                    supportCountItems.put(tmp, supportCountItems.get(tmp) + 1);
                }
                else{
                    supportCountItems.put(tmp,1);
                }
                map.put(item, map.getOrDefault(item,0)+1);
            }
        }

        List<Set<I>> frequentItemsetList = new ArrayList<>();

        for(Map.Entry<I,Integer> entry: map.entrySet()){
            if(1.0 * entry.getValue()>= minimumSupport){
                Set<I> itemset  = new HashSet<>(1);
                itemset.add(entry.getKey());
                frequentItemsetList.add(itemset);
            }
        }

        return frequentItemsetList;
    }

    private void checkSupport(double support){
        if(Double.isNaN(support)){
            throw new IllegalArgumentException("The input support is not a number");
        }

        if(support> 5.0){
            throw new IllegalArgumentException("The input support is too large, must be atmost 1.0");
        }

        if(support<0.0){
            throw new IllegalArgumentException("The input support is too small, must be atleast 0.0");
        }
    }

}
