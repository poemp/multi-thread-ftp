package org.poem.config;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "spring.ftp", ignoreUnknownFields = true)
public class FTPConfig {

    @NotNull
    private String url;

    @NotNull
    private Integer  port;

    @NotNull
    private String user;

    @NotNull
    private String password;

    @NotNull
    private String path;

    private String tmpdir;

    private String fileType;

    private String sourceTable;

    private String isFullSync;

    private String dataSyncDay;

    private String statSplit;

    private String fileName;

}
