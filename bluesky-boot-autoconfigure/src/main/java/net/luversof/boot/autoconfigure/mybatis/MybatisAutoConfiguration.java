package net.luversof.boot.autoconfigure.mybatis;

import java.io.IOException;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public interface MybatisAutoConfiguration {
	
	public default SqlSessionFactoryBean getSqlSessionFactoryBean() throws IOException {
		var sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:**/*Mapper.xml"));
		sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
		sqlSessionFactoryBean.setTypeHandlersPackage("net.luversof.opensource.mybatis.type");
//		sqlSessionFactoryBean.setTypeHandlersPackage("org.apache.ibatis.type");	// 이거 @MappedTypes 가 선언안되어 있네?
		return sqlSessionFactoryBean;
	}
}
