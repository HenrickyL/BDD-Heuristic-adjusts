# Pontos Importantes

1. Sempre olhar o **Uso máximo de memória**:  `-Xmx12g -Xms512m`
   * Intellij: `Run > Edit configurations` -> (Application/"RunMain"")`VM Options` : `-Xmx8g -Xms512m`
   * MVN: 
    ```bash
    $env:MAVEN_OPTS="-Xmx6g -Xms512m"
    mvn compile exec:java
    ```