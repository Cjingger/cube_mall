package com.cubemall.mybatis;

import com.cubemall.search.CubemallSearchApplication;
import com.cubemall.search.dao.SpuInfoDao;
import com.cubemall.search.entity.SpuInfoEntity;
import com.cubemall.search.model.SpuInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CubemallSearchApplication.class)
public class MybatisTest {

    @Autowired
    private SpuInfoDao spuInfoDao;

    @Test
    public void testMybatis(){
        SpuInfoEntity spuInfoEntity = spuInfoDao.selectById(5468722);
        System.out.println(spuInfoEntity);

    }

    @Test
    public void testGetSpuInfoById(){
        SpuInfo spuInfoById = spuInfoDao.getSpuInfoById(5468722L);
        System.out.println(spuInfoById);

    }

}
