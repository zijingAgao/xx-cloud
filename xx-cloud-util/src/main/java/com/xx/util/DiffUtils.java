package com.xx.util;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * diff集合差异,适用于对比两边数据差异
 * 得到新增/修改/删除/未变更 的结果
 *
 * @author Agao
 * @date 2025/11/3 15:16
 */
public class DiffUtils<T> {

    /**
     * 对比两个集合对象差异
     *
     * @param newList     新值
     * @param oldList     老值
     * @param keyFunction 唯一key
     * @return diff结果
     */
    public Diff<T> diff(List<T> newList, List<T> oldList, Function<T, Object> keyFunction) {
        return diff(newList, oldList, keyFunction, Object::equals);
    }

    /**
     * 对比两个集合对象差异
     *
     * @param newList      新值
     * @param oldList      老值
     * @param keyFunction  唯一key
     * @param diffFunction diff差异函数, 注意: 第一个入参是新值，第二个入参是老值
     * @return diff结果
     */
    public Diff<T> diff(List<T> newList, List<T> oldList, Function<T, Object> keyFunction, BiFunction<T, T, Boolean> diffFunction) {
        Diff<T> diff = new Diff<>();
        // 全量删除
        if (CollectionUtils.isEmpty(newList)) {
            diff.setDel(oldList);
            return diff;
        }

        // 全量新增
        if (CollectionUtils.isEmpty(oldList)) {
            diff.setAdd(newList);
            return diff;
        }

        Map<Object, T> oldMap = new HashMap<>();
        oldList.forEach(item -> oldMap.put(keyFunction.apply(item), item));

        for (T newItem : newList) {
            Object key = keyFunction.apply(newItem);
            T oldItem = oldMap.get(key);

            if (oldItem == null) {
                // add
                diff.getAdd().add(newItem);
                continue;
            }
            // diff
            if (diffFunction.apply(newItem, oldItem)) {
                diff.getInDiff().add(newItem);
            } else {
                diff.getUpdate().add(newItem);
            }
            oldMap.remove(key);
        }

        // del
        diff.getDel().addAll(oldMap.values());

        return diff;
    }


    /**
     * @author Agao
     * @date 2025/11/3 15:16
     */
    @Data
    public static class Diff<T> {
        /**
         * 新增
         */
        private List<T> add = new ArrayList<>();
        /**
         * 修改
         */
        private List<T> update = new ArrayList<>();
        /**
         * 删除
         */
        private List<T> del = new ArrayList<>();
        /**
         * 无差异
         */
        private List<T> inDiff = new ArrayList<>();
    }

}
