package com.raegon.example.jpa.log;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.Locale;

@Component
public class P6SpyFormatter extends JdbcEventListener implements MessageFormattingStrategy {

  @Override
  public void onAfterGetConnection(ConnectionInformation connectionInformation, SQLException e) {
    P6SpyOptions.getActiveInstance().setLogMessageFormat(getClass().getName());
  }

  @Override
  public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
    StringBuilder sb = new StringBuilder();
    sb.append("took ").append(elapsed).append("ms, ").append(category);
    if (StringUtils.hasText(sql)) {
      sb.append(formatQuery(sql));
    }
    return sb.toString();
  }

  private String formatQuery(String sql) {
    String formatted;
    if (isDDL(sql)) {
      formatted = FormatStyle.DDL.getFormatter().format(sql);
    } else {
      formatted = FormatStyle.BASIC.getFormatter().format(sql);
    }
    return FormatStyle.HIGHLIGHT.getFormatter().format(formatted);
  }

  private boolean isDDL(String query) {
    query = query.trim().toLowerCase(Locale.ROOT);
    return query.startsWith("create") || query.startsWith("alter") || query.startsWith("comment");
  }

}
