package com.wangtao.dbhelper.reflection.typeparam;

import java.util.List;
import java.util.Map;

public interface Level0Mapper<L, M, N> {

    void simpleSelectVoid(Integer param);

    double simpleSelectPrimitive(int param);

    Double simpleSelect();

    List<Double> simpleSelectList();

    Map<Integer, Double> simpleSelectMap();

    String[] simpleSelectArray();

    String[][] simpleSelectArrayOfArray();

    <K extends Calculator<?>> K simpleSelectTypeVar();

    List<? extends String> simpleSelectWildcard();

    N select(N param);

    List<N> selectList(M param1, N param2);

    List<? extends N> selectWildcardList();

    Map<N, M> selectMap();

    N[] selectArray(List<N>[] param);

    N[][] selectArrayOfArray();

    List<N>[] selectArrayOfList();

    Calculator<N> selectCalculator(Calculator<N> param);

    List<Calculator<L>> selectCalculatorList();

    interface Level0InnerMapper extends Level0Mapper<String, Long, Float> {
    }

}
