package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class DbmlEntityCoverageTest {
    private static final Pattern TABLE_PATTERN = Pattern.compile("(?m)^Table\\s+([a-z_]+)\\s+\\{");

    @Test
    void everyDbmlTableHasEntityAndRepository() throws Exception {
        Path root = Path.of("").toAbsolutePath();
        String dbml = Files.readString(root.resolve("docs/db.dbml"));
        Set<String> missingEntities = new LinkedHashSet<>();
        Set<String> missingRepositories = new LinkedHashSet<>();

        Matcher matcher = TABLE_PATTERN.matcher(dbml);
        while (matcher.find()) {
            String javaName = toJavaName(matcher.group(1));
            Path entityPath = root.resolve("src/main/java/vn/ezisolutions/cloud/hackathon/modules/orderflow/common/domain/" + javaName + "Entity.java");
            Path repositoryPath = root.resolve("src/main/java/vn/ezisolutions/cloud/hackathon/modules/orderflow/common/infrastructure/" + javaName + "Repository.java");
            if (!Files.isRegularFile(entityPath)) {
                missingEntities.add(entityPath.getFileName().toString());
            }
            if (!Files.isRegularFile(repositoryPath)) {
                missingRepositories.add(repositoryPath.getFileName().toString());
            }
        }

        assertThat(missingEntities).isEmpty();
        assertThat(missingRepositories).isEmpty();
    }

    private String toJavaName(String tableName) {
        StringBuilder builder = new StringBuilder();
        for (String part : tableName.split("_")) {
            if ("aliases".equals(part)) {
                part = "alias";
            } else if ("warehouses".equals(part)) {
                part = "warehouse";
            } else if (part.endsWith("s")) {
                part = part.substring(0, part.length() - 1);
            }
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return builder.toString();
    }
}
