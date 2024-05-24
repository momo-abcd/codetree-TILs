import java.util.*;
import java.io.*;


public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringBuilder sb = new StringBuilder();
    static StringTokenizer st = null;

    static final int EMPTY_SPACE = 0; // 빈 공간
    static final int WALL = -1; // 벽
    static final int JECHO = -2; // 제초제
    static int[] dr = new int[] {0,0,1,-1};
    static int[] dc = new int[] {1,-1,0,0};

    static int[] dq = new int[] {-1,1,1,-1};
    static int[] dp = new int[] {-1,-1,1,1};

    static int ans = 0;

    static int
            n, // 격자의 크기
            m, // 박멸 진행되는 년 수
            k, // 제초제의 확산 범위
            C; // 제초제가 남아 있는 년수

    static int[][][] arr;

    public static void main(String[] args) throws Exception {
        // 입렫 받기
        input();

        // m년동안 진행
        for(int i=0; i<m;i++) {
            // 시간 지난 제초제 제거
            // 나무의 성장
//            System.out.println("grow()");
            grow();
//            print();
//            System.out.println("birth()");
            birth();
//            print();
//            System.out.println("spread()");
            spread();
//            print();
            time();
        }
        System.out.println(ans);
    }

    static void time() {
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                if(arr[i][j][0] == JECHO) {
                    // 제초제의 남은 년수가 0이면 빈 공간으로 바꿔줌
                    if(--arr[i][j][1] == 0) {
                        arr[i][j][0] = EMPTY_SPACE;
                    }
                }
            }
        }
    }

    static void spread() {
        // 1. 나무를 가장 많이 죽일 수 있는 위치를 찾는다.
        int max = Integer.MIN_VALUE;
        int mr = 0;
        int mc = 0;
        for(int r=0;r<n;r++) {
            for (int c=0;c<n;c++) {
                if(arr[r][c][0] < 1) continue;
                int val = findBFS(new int[] {r,c});
//                System.out.println("-------------------------");
//                System.out.printf("[%d, %d] : %d \n" ,r,c, val);
//                System.out.println("-------------------------");

                if(val > max) {
                    max = val;
                    mr = r;
                    mc = c;
//                    System.out.printf("제초제 많이 뿌리는 년도 [%d, %d] : %d \n",r, c,val);
                }
            }
        }
        copyArr(spread_2(mr, mc),arr);
        ans += max;
    }

    // 완탐으로 나무를 가장 많이 죽이는 공간을 찾는 BFS 함수
    static int findBFS(int[] start) {
        // 배열 복사
        int[][][] newArr = new int[n][n][2];
        copyArr(arr, newArr);

        int result = 0;
//        visited[start[0]][start[1]] = true;

//        while(!q.isEmpty()) {
//            int[] pos = q.poll();
            int r = start[0];
            int c = start[1];
            result += arr[r][c][0];
            newArr[r][c][0] = JECHO;
            newArr[r][c][1] = C;
//            arr[r][c][1] =C;

            // 대각선 사방향 탐색
            for(int i=0;i<4;i++) {
                int j = k;
                int nr = r;
                int nc = c;
                while(j-- > 0) {
                    nr = nr + dq[i];
                    nc = nc + dp[i];

                    // 경계 체크
                    if(nr < 0 || nr >= n || nc < 0 || nc >= n) break;

                    // 벽이라면 그만 둠
                    if(arr[nr][nc][0] == WALL) break;

                    // 빈 공간 혹은 제초제가 뿌려진 곳이라면
                    if(arr[nr][nc][0] == EMPTY_SPACE || arr[nr][nc][0] == JECHO) {
//                    visited[nr][nc] = true;
                        newArr[nc][nr][0] = JECHO;
                        newArr[nc][nr][1] = C + 1;
                        break;
                    }
                    // 나무가 있다면
                    if(arr[nr][nc][0] > 0 && arr[nr][nc][0] <= 100) {
                        result += arr[nr][nc][0];
//                        System.out.println("------------------------------");
//                        print();
//                        System.out.println("------------------------------");
//                        System.out.printf("[%d, %d] : %d \n", nr, nc, arr[nr][nc][0]);
//                        System.out.println("------------------------------");
                        newArr[nc][nr][0] = JECHO;
                        newArr[nc][nr][1] = C + 1;
                    }
                }
            }
//        }
        // 제초제 뿌리면 죽는 나무 그루의 수 반환
        return result;
    }

    static int[][][] spread_2(int startX, int startY) {
        int[][][] newArr = new int[n][n][2];
        copyArr(arr, newArr);
        for(int i=0;i<4;i++) {
            int j = k;
            int nr = startX;
            int nc = startY;
            while(j-- > 0) {
                nr = nr + dq[i];
                nc = nc + dp[i];

                // 경계 체크
                if(nr < 0 || nr >= n || nc < 0 || nc >= n) continue;

                // 벽이라면 그만 둠
                if(arr[nr][nc][0] == WALL) break;

                // 빈 공간이거나 제초제가 있는곳이라면
                if(arr[nr][nc][0] == EMPTY_SPACE || arr[nr][nc][0] == JECHO) {
//                    visited[nr][nc] = true;
                    newArr[nr][nc][0] = JECHO;
                    newArr[nr][nc][1] = C + 1;
                    break;
                }
                // 나무가 있다면
                if(arr[nr][nc][0] > 0 && arr[nr][nc][0] <= 100) {
                    newArr[nr][nc][0] = JECHO;
                    newArr[nr][nc][1] = C + 1;
                }
            }
        }
        newArr[startX][startY][0] = JECHO;
        newArr[startX][startY][1] = C + 1;
        return newArr;
    }

    static void birth() {
        // 배열 복제
        int[][][] newArr = new int[n][n][2];
        copyArr(arr,newArr);

        // 나무의 번식
        for(int r=0;r<n;r++) {
            for(int c=0;c<n;c++) {
                // 나무가 없으면 건너 띔
                if(arr[r][c][0] < 1 ) continue;
                int div = 0;
                // 번식 가능한 곳의 개수를 찾음
                for(int i=0;i<4;i++) {
                    int nr = r + dr[i];
                    int nc = c + dc[i];

                    // 경계 밖 확인
                    if(nr < 0 || nr >= n || nc < 0 || nc >= n ) continue;
                    // 빈공간이면 div 증가
                    if(arr[nr][nc][0] == EMPTY_SPACE)  div++;
                }
                for(int i=0;i<4;i++) {
                    int nr = r + dr[i];
                    int nc = c + dc[i];

                    // 경계 밖 확인
                    if(nr < 0 || nr >= n || nc < 0 || nc >= n ) continue;
                    // 빈공간이거나 나무가 있다면 번식해야 할 만큼 증가시킴
                    if(arr[nr][nc][0] == 0)
                        newArr[nr][nc][0] += arr[r][c][0] / div;
                }
            }
        }

        // 번식 된 배열을 원배열로 다시 옮겨줌
//        for(int i=0;i<n;i++) {
//            for(int j=0;j<n;j++) {
//                arr[i][j][0] = newArr[i][j][0];
//            }
//        }
        copyArr(newArr, arr);
    }

    static void grow() {
        for(int r=0;r<n;r++) {
            for(int c=0;c<n;c++) {
                if(arr[r][c][0] < 1) continue;
                for(int i=0;i<4;i++) {
                    int nr = r + dr[i];
                    int nc = c + dc[i];

                    // 보드의 범위를 벗어나는지 확인
                    if(nr < 0 || nr >= n || nc <0 || nc >=n) continue;
                    // 나무 인지 체크
                    if(arr[nr][nc][0] > 0) {
                        arr[r][c][0]++; // 나무 그루 수 하나 증가
                    }
                }
            }
        }
    }



    // 입력 받기 함수
    static void input() throws Exception {
        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());

        arr = new int[n][n][2];

        for(int i=0;i<n;i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=0;j<n;j++) {
                arr[i][j][0] = Integer.parseInt(st.nextToken());
            }
        }
    }


    static void copyArr(int[][][] source, int[][][]destination) {
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                destination[i][j][0] = source[i][j][0];
                destination[i][j][1] = source[i][j][1];
            }
        }
    }

    static void print() {
        for(int i=0; i<n;i++) {
            for(int j=0;j<n;j++) {
                System.out.printf("%2d ",arr[i][j][0]);
            }
            System.out.println();
        }
        System.out.println("---------------------");
    }
}