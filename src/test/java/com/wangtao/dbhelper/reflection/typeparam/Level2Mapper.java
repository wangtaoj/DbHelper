package com.wangtao.dbhelper.reflection.typeparam;

import java.io.Serializable;
import java.util.Date;

public interface Level2Mapper extends Level1Mapper<Date, Integer>, Serializable, Comparable<Integer>{
}
