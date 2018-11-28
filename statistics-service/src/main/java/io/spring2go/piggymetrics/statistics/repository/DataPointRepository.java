package io.spring2go.piggymetrics.statistics.repository;

import io.spring2go.piggymetrics.statistics.CatAnnotation;
import io.spring2go.piggymetrics.statistics.domain.timeseries.DataPoint;
import io.spring2go.piggymetrics.statistics.domain.timeseries.DataPointId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataPointRepository extends CrudRepository<DataPoint, DataPointId> {

	@CatAnnotation
	List<DataPoint> findByIdAccount(String account);

}
