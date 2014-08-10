/**
 * 
 */
package org.minnal.core.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.Collection;

import org.minnal.core.MinnalException;
import org.minnal.core.config.MediaTypeMixin;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class DefaultYamlSerializer extends Serializer {
	
	private ObjectMapper mapper;
	
	public DefaultYamlSerializer() {
		this(createSimpleModule());
	}
	
	public DefaultYamlSerializer(ObjectMapper mapper) {
		this(mapper, createSimpleModule());
	}
	
	public DefaultYamlSerializer(Module module) {
		this(new ObjectMapper(new YAMLFactory()), module);
	}
	
	protected DefaultYamlSerializer(ObjectMapper mapper, Module module) {
		this.mapper = mapper;
		init(module);
	}
	
	private static Module createSimpleModule() {
		SimpleModule module = new SimpleModule();
		module.addKeySerializer(MediaType.class, new JsonSerializer<MediaType>() {
			@Override
			public void serialize(MediaType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
				jgen.writeFieldName(String.valueOf(value.withoutParameters().toString()));
			}
		});;
		return module;
	}
	
	protected void init(Module module) {
		mapper.addMixInAnnotations(MediaType.class, MediaTypeMixin.class);
		mapper.registerModule(module);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	public ByteBuf serialize(Object object) {
		ByteBuf buffer = Unpooled.buffer();
		ByteBufOutputStream os = new ByteBufOutputStream(buffer);
		try {
			mapper.writeValue(os, object);
		} catch (Exception e) {
			throw new MinnalException("Failed while serializing the object", e);
		}
		return buffer;
	}

	public <T> T deserialize(ByteBuf buffer, Class<T> targetClass) {
		ByteBufInputStream is = new ByteBufInputStream(buffer);
		try {
			return mapper.readValue(is, targetClass);
		} catch (Exception e) {
			throw new MinnalException("Failed while deserializing the buffer to type - " + targetClass, e);
		}
	}
	
	@Override
	public <T extends Collection<E>, E> T deserializeCollection(ByteBuf buffer, Class<T> collectionType, Class<E> elementType) {
		ByteBufInputStream is = new ByteBufInputStream(buffer);
		JavaType javaType = mapper.getTypeFactory().constructCollectionType(collectionType, elementType);
		try {
			return mapper.readValue(is, javaType);
		} catch (Exception e) {
			throw new MinnalException("Failed while deserializing the buffer to type - " + javaType, e);
		}
	}
}
