package br.com.rodrigo.ecommerce.consumer;

public interface ServiceFactory<T> {
    ConsumerService<T> create() throws Exception;
}
