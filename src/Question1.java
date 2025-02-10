import java.util.*;
import java.util.stream.Collectors;

public class Question1 {
    // 초기 카테고리 데이터 세팅
    public static List<int[]> relationships = Arrays.asList(
            new int[]{-1, 1},
            new int[]{1, 2},
            new int[]{2, 3},
            new int[]{2, 4},
            new int[]{2, 5},
            new int[]{2, 6},
            new int[]{1, 7},
            new int[]{7, 8},
            new int[]{7, 9},
            new int[]{7, 10},
            new int[]{-1, 11},
            new int[]{11, 12},
            new int[]{12, 9},
            new int[]{12, 13},
            new int[]{12, 14}
    );
    public static Map<Integer, String> categoryName = new HashMap<Integer, String>() {{
        put(1, "남자");
        put(2, "엑소");
        put(3, "공지사항");
        put(4, "첸");
        put(5, "백현");
        put(6, "시우민");
        put(7, "방탄소년단");
        put(8, "공지사항");
        put(9, "익명게시판");
        put(10, "뷔");
        put(11, "여자");
        put(12, "블랙핑크");
        put(13, "공지사항");
        put(14, "로제");
    }};

    public static Map<Integer, Integer> boardNos = new HashMap<>() {{
        put(3, 1);
        put(4, 2);
        put(5, 3);
        put(6, 4);
        put(8, 5);
        put(9, 6);
        put(10, 7);
        put(13, 8);
        put(14, 9);
    }};

    public static class CategoryNode {
        private int id;
        private String name;
        private int boardNo;
        private List<CategoryNode> childrens;

        public CategoryNode(int id, String name) {
            this.id = id;
            this.name = name;
            this.boardNo = 0;
            this.childrens = new ArrayList<>();
        }

        public void setBoardNo(int boardNo) {
            this.boardNo = boardNo;
        }

        /**
         * Json 형태의 문자열로 파싱하는 메서드
         * @return jsonBuilder.toString()
         */
        public String toJson() {
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"id\":\"").append(id).append("\",");
            jsonBuilder.append("\"name\":\"").append(name).append("\"");

            // boardNo가 존재하면 추가
            if (boardNo != 0) {
                jsonBuilder.append(",\"boardNo\":\"").append(boardNo).append("\"");
            }

            // 자식 노드가 존재하면 childrens 배열 추가
            if (!childrens.isEmpty()) {
                jsonBuilder.append(",\"childrens\":[");
                StringJoiner childJsons = new StringJoiner(",");
                for (CategoryNode child : childrens) {
                    childJsons.add(child.toJson());
                }
                jsonBuilder.append(childJsons);
                jsonBuilder.append("]");
            }

            jsonBuilder.append("}");
            return jsonBuilder.toString();
        }

        /**
         * 아이디(식별자)를 변수로 받아 해당 아이디와 일치하는 카테고리를 반환
         *
         * @param searchId
         * @return result
         */
        public CategoryNode findById(int searchId) {
            if (this.id == searchId) {
                return this;
            }
            for (CategoryNode child : childrens) {
                CategoryNode result = child.findById(searchId);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

        /**
         * 카테고리명을 변수로 받아 해당 카테고리명과 일치하는 모든 카테고리를 반환
         *
         * @param searchName
         * @return result
         */
        public Set<CategoryNode> findAllByName(String searchName) {
            // 이름과 아이디가 모두 같은 노드가 추가되어도 중복을 제거할 수 있도록 Set 사용 ex)익명게시판
            Set<CategoryNode> result = new HashSet<>();
            if (this.name.equals(searchName)) {
                result.add(this);
            }

            for (CategoryNode child : childrens) {
                result.addAll(child.findAllByName(searchName));
            }

            return result;
        }
    }

    /**
     * 아이디(식별자)를 바탕으로 검색한 결과 값을 출력
     *
     * @param rootCategories
     * @param searchId
     */
    public static void printSearchResultById(List<CategoryNode> rootCategories, int searchId) {
        for (CategoryNode rootCategory : rootCategories) {
            CategoryNode result = rootCategory.findById(searchId);
            if (result != null) {
                System.out.println(result.toJson());
                break;
            }
        }
    }

    /**
     * 카테고리명을 바탕으로 검색한 결과 값을 출력
     *
     * @param rootCategories
     * @param searchName
     */
    public static void printSearchResultsByName(List<CategoryNode> rootCategories, String searchName) {
        Set<CategoryNode> result = new HashSet<>();

        for (CategoryNode rootCategory : rootCategories) {
            result.addAll(rootCategory.findAllByName(searchName));
        }
        if (!result.isEmpty()) {
            for (CategoryNode categoryNode : result) {
                System.out.println(categoryNode.toJson());
            }
        }
    }

    /**
     * 카테고리들의 관계, 이름, 게시판 번호 정보 변수를 파라미터로 받아 카테고리의 계층구조를 만드는 함수
     *
     * @param relationships
     * @param categoryNames
     * @param boardNos
     * @return rootCategories
     */
    public static List<CategoryNode> buildCategories(List<int[]> relationships, Map<Integer, String> categoryNames, Map<Integer, Integer> boardNos) {
        // 가장 최상위(root)에 존재하는 카테고리들을 저장하여 return
        List<CategoryNode> rootCategories = new ArrayList<>();

        // 기존에 초기화 된 id와 이름을 바탕으로 카테고리를 전부 만들어 먼저 저장을 한다
        Map<Integer, CategoryNode> categories = new HashMap<>();
        for (Map.Entry<Integer, String> category : categoryNames.entrySet()) {
            categories.put(category.getKey(), new CategoryNode(category.getKey(), category.getValue()));
        }

        // 관계 배열을 바탕으로 노드들의 관계를 세팅
        for (int[] relationship : relationships) {
            int parent_idx = relationship[0];
            int child_id = relationship[1];

            CategoryNode child = categories.get(child_id);
            // boardNos에는 boardNo가 지정되어야 하는 카테고리들의 id가 들어가있음
            if (boardNos.containsKey(child_id)) {
                child.setBoardNo(boardNos.get(child_id));
            }

            // parent_idx == -1 이면 루트
            if (parent_idx == -1) {
                rootCategories.add(child);
            } else {
                CategoryNode parent = categories.get(parent_idx);
                parent.childrens.add(child);
            }

        }
        return rootCategories;
    }

    public static void main(String[] args) {
        System.out.println("---------------------Start Question 1---------------------");
        List<CategoryNode> rootCategories = buildCategories(relationships, categoryName, boardNos);

        System.out.println("1. 전체 출력 테스트");
        for (CategoryNode rootCategory : rootCategories) {
            System.out.println(rootCategory.toJson());
        }

        System.out.println("2. 식별자(아이디) 검색 테스트 : 예시 아이디 - 11");
        int searchId = 11;
        printSearchResultById(rootCategories, searchId);

        System.out.println("3. 카테고리명 검색 테스트 : 예시 카테고리명 - 공지사항");
        String searchName = "공지사항";
        printSearchResultsByName(rootCategories, searchName);

        System.out.println("----------------------End Question 1----------------------");
    }
}
