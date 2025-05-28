package org.ufc.planner.app;

// Testar o uso de memória -Xmx6g -Xms512m

public class MemoryTest {
    public static void main(String[] args) {
        System.out.println("Iniciando teste de alocação de memória...");

        try {
            int size = 500 * 1024 * 1024; // ~500MB
            byte[][] memoryBlocks = new byte[20][];
            for (int i = 0; i < memoryBlocks.length; i++) {
                memoryBlocks[i] = new byte[size];
                int index = i+1;
                System.out.println("Alocado bloco " + (index) + ": ~" + (size / 1024 / 1024) + "MB");
                Thread.sleep(500);
            }
        } catch (OutOfMemoryError e) {
            System.err.println("OutOfMemoryError: Heap excedida!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("✅ Teste concluído.");
    }
}
