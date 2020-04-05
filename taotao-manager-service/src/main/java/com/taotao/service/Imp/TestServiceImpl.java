package com.taotao.service.Imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.mapper.TestMapper;
import com.taotao.service.TestService;

@Service(value = "testServiceImpl")
public class TestServiceImpl implements TestService {
	@Autowired
	private TestMapper testmapper;
	//返回一个一个数据库中的时间
	@Override
	public String queryNow() {
		System.out.println("testmapper");
		return testmapper.queryNow();
	}
}
