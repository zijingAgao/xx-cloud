package com.xx.pojo;

import com.xx.vo.UserVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataChunk implements Comparable<DataChunk> {
    private int index; // 线程的索引，用于保证写入顺序
    private List<UserVo> data; // 查询的数据
    @Override
    public int compareTo(DataChunk other) {
        return Integer.compare(this.index, other.index);
    }
}
