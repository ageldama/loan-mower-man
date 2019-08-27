package jhyun.loanmowerman.storage.repositories;

import jhyun.loanmowerman.storage.entities.TrainedPredictionModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TrainedPredictionModelRepository extends PagingAndSortingRepository<TrainedPredictionModel, Integer> {
    Optional<TrainedPredictionModel> findByStrategyAndKey(String strategy, String key);
}
