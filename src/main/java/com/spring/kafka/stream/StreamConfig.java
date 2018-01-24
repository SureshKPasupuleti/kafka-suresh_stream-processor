package com.spring.kafka.stream;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.KStreamBuilderFactoryBean;


import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class StreamConfig { 

    @Value("${kafkaStream.bootStrapServer}")
    private String bootStrapServer;

    @Value("${kafkaStream.applicationId}")
    private String applicationId;

    @Value("${kafkaStream.producerTopic}")
    private String producerTopic;

    @Value("${kafkaStream.consumerTopic}")
    private String consumerTopic;

    @Value("${kafkaStream.groupEmailID}")
    private String groupEmailID;


	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public StreamsConfig kStreamsConfigs() {
		Map<String, Object> props = new HashMap<>();
		//props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "104.42.108.238:9092");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
		props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
		return new StreamsConfig(props);
	}
	@Bean(name = "defaultKStreamBuilder")
	public FactoryBean<KStreamBuilder> myKStreamBuilder(StreamsConfig streamsConfig) {
		return new KStreamBuilderFactoryBean(streamsConfig);
	}
	@Bean
	public KStream<Integer, String> kStream(KStreamBuilder kStreamBuilder) {
		KStream<Integer, String> stream = kStreamBuilder.stream(producerTopic);
		stream.mapValues(String -> ObjectUpdateMethod(String)).
		to(consumerTopic);
		stream.print();

		return stream;
	}
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	private String ObjectUpdateMethod(String arg0) {
		String result="";
		try {
			Employer employer=objectMapper().readValue(arg0, Employer.class);
			Employer employerUpdate=new Employer();
			employerUpdate.setKey(employer.getKey());
			employerUpdate.setFirstName(employer.getFirstName());
			employerUpdate.setLastName(employer.getLastName());
			employerUpdate.setEmployeeEmailAddress(employer.getEmployeeEmailAddress());
			employerUpdate.setGroupEmailAddress(groupEmailID);
			employerUpdate.setEnrolledForBenefits(employer.getEnrolledForBenefits());
			employerUpdate.setEnrolledForMedicalInsurance(employer.getEnrolledForMedicalInsurance());
			employerUpdate.setLocation(employer.getLocation());
			employerUpdate.setSignedOnEmployeeAgreement(employer.getSignedOnEmployeeAgreement());
			employerUpdate.setSignedOnHarrassementPolicy(employer.getSignedOnHarrassementPolicy());
			employerUpdate.setPhoneNumber(employer.getPhoneNumber());
			//kafkaVOUpdate.setVersion(kafkaVO.getVersion()+"..Updated after stream processing");
			result=objectMapper().writeValueAsString(employerUpdate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
