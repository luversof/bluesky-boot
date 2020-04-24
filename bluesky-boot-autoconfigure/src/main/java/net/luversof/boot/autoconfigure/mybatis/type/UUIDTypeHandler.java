package net.luversof.boot.autoconfigure.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

//@MappedJdbcTypes(JdbcType.BINARY)
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter.toString());
	}

	@Override
	public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
		var value = rs.getString(columnName);
		if (value != null)
			return UUID.fromString(value);
		return null;
	}

	@Override
	public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		var value = rs.getString(columnIndex);
		if (value != null)
			return UUID.fromString(value);
		return null;
	}

	@Override
	public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		var value = cs.getString(columnIndex);
		if (value != null)
			return UUID.fromString(value);
		return null;
	}

}