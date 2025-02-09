import java.util.Scanner;

public class Question2 {
    public static void main(String[] args) {
        System.out.println("---------------------Start Question 2---------------------");

        Scanner scanner = new Scanner(System.in);

        System.out.print("sum = ");
        int sum = scanner.nextInt();

        System.out.print("coins[] = ");
        scanner.nextLine();
        String[] coinStrings = scanner.nextLine().replace("{","").replace("}", "").split(",");
        int[] coins = new int[coinStrings.length];

        for (int i = 0; i < coinStrings.length; i++) {
            coins[i] = Integer.parseInt(coinStrings[i]);
        }

        System.out.printf("%d\n",countWays(sum, coins));

        scanner.close();
    }

    /**
     * sum을 만드는 방법의 수를 계산하는 함수
     * @param sum
     * @param coins
     * @return ways[sum]
     */
    public static int countWays(int sum, int[] coins) {
        // ways[i] = i를 만드는 방법의 수
        int[] ways = new int[sum + 1];
        ways[0] = 1; // 0을 만드는 방법은 1가지(동전을 사용하지 않음)

        // 각 동전 별로 i를 만드는 방법의 수를 계산하도록
        for (int coin : coins) {
            for (int i = coin; i <= sum; i++) {
                ways[i] += ways[i - coin];
            }
        }
        return ways[sum];
    }
}