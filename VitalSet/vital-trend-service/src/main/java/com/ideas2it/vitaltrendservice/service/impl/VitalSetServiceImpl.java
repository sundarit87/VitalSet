package com.ideas2it.vitaltrendservice.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ideas2it.vitaltrendservice.custom.LogExecutionTime;
import com.ideas2it.vitaltrendservice.exception.NoDataFoundException;
import com.ideas2it.vitaltrendservice.exception.ResourceNotFoundException;
import com.ideas2it.vitaltrendservice.model.PayloadRequest;
import com.ideas2it.vitaltrendservice.model.VitalSet;
import com.ideas2it.vitaltrendservice.repository.VitalSetRepository;
import com.ideas2it.vitaltrendservice.service.VitalSetService;

@Service
public class VitalSetServiceImpl implements VitalSetService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String TOPIC = "Kafka_logs";
	LocalDateTime currentTime = LocalDateTime.now();
	@Autowired
	private KafkaTemplate<String, PayloadRequest> kafkaTemplate;

	private VitalSetRepository vitalSetRepository;

	public VitalSetServiceImpl(VitalSetRepository vitalSetRepository) {
		this.vitalSetRepository = vitalSetRepository;
	}
	@CachePut(cacheNames="vitalSet",key = "#id")
	@Override
	public VitalSet createVitalSet(VitalSet vitalSet) {
		return vitalSetRepository.save(vitalSet);
	}
	@Cacheable(cacheNames="vitalSet")
	@LogExecutionTime
	@Override
	public List<VitalSet> findAll() {
		List<VitalSet> vitalSet = vitalSetRepository.findAll();
		if (vitalSet.isEmpty())
			throw new NoDataFoundException();
		logger.info("finding all vital set in Service - " + currentTime);
		return vitalSet;
	}

	@CacheEvict(cacheNames="vitalSet", key = "#id")
	@Override
	public Map<String, Boolean> deleteById(long id) throws ResourceNotFoundException{
		Optional<VitalSet> optionalVitalSet = vitalSetRepository.findById(id);
		if (optionalVitalSet.isPresent()) {
			vitalSetRepository.deleteById(id);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return response;
		} else {
			throw new ResourceNotFoundException(id);
		}

	}
	@Cacheable(cacheNames="vitalSet", key="#id")
	@Override
	public VitalSet findById(long id) throws ResourceNotFoundException{
		Optional<VitalSet> optionalVitalSet = vitalSetRepository.findById(id);
		logger.info("finding vital set by id in Service : "+id +" - " + currentTime);
		if (optionalVitalSet.isPresent()) {
			return optionalVitalSet.get();
		} else {
			throw new ResourceNotFoundException(id);
		}
	}
	@CachePut(cacheNames="vitalSet",key = "#id")
	@LogExecutionTime
	@Override
	public VitalSet updateVitalSet(long id, VitalSet vitalSet) throws ResourceNotFoundException{
		Optional<VitalSet> optionalVitalSet = vitalSetRepository.findById(id);
		if (optionalVitalSet.isPresent()) {
			VitalSet newVitalSet = optionalVitalSet.get();
			newVitalSet.setUsername(vitalSet.getUsername());
			newVitalSet.setPatientFirstName(vitalSet.getPatientFirstName());
			newVitalSet.setPatientLastName(vitalSet.getPatientLastName());
			newVitalSet.setSystolic(vitalSet.getSystolic());
			newVitalSet.setDiastolic(vitalSet.getDiastolic());
			newVitalSet.setPulse(vitalSet.getPulse());
			newVitalSet.setRespirations(vitalSet.getRespirations());
			newVitalSet.setSpo2(vitalSet.getSpo2());
			newVitalSet.setTemperature(vitalSet.getTemperature());
			newVitalSet.setDate(vitalSet.getDate());
			newVitalSet.setTime(vitalSet.getTime());

			return vitalSetRepository.save(newVitalSet);

		} else {
			throw new ResourceNotFoundException(id);
		}
	}

	@Override
	public void sendMessage(PayloadRequest message) {
		System.out.println(message.toString());
		kafkaTemplate.send(TOPIC, message);
	}

}
