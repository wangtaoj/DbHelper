package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.domian.Address;
import com.wangtao.dbhelper.domian.AddressVO;

/**
 * @author wangtao
 * Created at 2019/2/28 14:31
 */
public interface AddressMapper {

    AddressVO findById(Integer id);

    AddressVO findByIdAutoMapping(Integer id);

    int insert(Address address);

    int count();
}
