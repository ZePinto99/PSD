package Representation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Top5District {
    public final int id;
    public final String top1;
    public final String top2;
    public final String top3;
    public final String top4;
    public final String top5;

    @JsonCreator
    public Top5District(@JsonProperty("Status") int id, @JsonProperty("Top 1") String top1,
                       @JsonProperty("Top 2") String top2,
                       @JsonProperty("Top 3") String top3,
                       @JsonProperty("Top 4") String top4,
                       @JsonProperty("Top 5") String top5) {
        this.id = id;
        this.top1 = top1;
        this.top2 = top2;
        this.top3 = top3;
        this.top4 = top4;
        this.top5 = top5;
    }

    @JsonProperty("Status")
    public int getId() {
        return id;
    }

    @JsonProperty("Top 1")
    public String getTop1() {
        return top1;
    }

    @JsonProperty("Top 2")
    public String getTop2() {
        return top2;
    }

    @JsonProperty("Top 3")
    public String getTop3() {
        return top3;
    }

    @JsonProperty("Top 4")
    public String getTop4() {
        return top4;
    }

    @JsonProperty("Top 5")
    public String getTop5() {
        return top5;
    }
}
