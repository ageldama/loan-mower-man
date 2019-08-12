package jhyun.loanmowerman.storage.entities;

import lombok.*;

import javax.persistence.*;

@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "trained_prediction_models",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "strategy", "key"
        }))
public class TrainedPredictionModel {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "trained_prediction_models_id_seq")
    @Column(name = "id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "strategy", nullable = false)
    private String strategy;

    @Getter
    @Setter
    @Column(name = "key", nullable = false)
    private String key;

    @Getter
    @Setter
    @Column(name = "data")
    private byte[] data;

    public TrainedPredictionModel() {
    }

    public TrainedPredictionModel(Integer id, String strategy, String key, byte[] data) {
        this.id = id;
        this.strategy = strategy;
        this.key = key;
        this.data = data;
    }
}
