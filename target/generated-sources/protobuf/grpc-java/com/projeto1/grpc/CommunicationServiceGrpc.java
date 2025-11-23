package com.projeto1.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Define o serviço de comunicação que será implementado pelo servidor e chamado pelo cliente.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.65.1)",
    comments = "Source: communication.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CommunicationServiceGrpc {

  private CommunicationServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "communication.CommunicationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.projeto1.grpc.RequestMessage,
      com.projeto1.grpc.ResponseMessage> getSendMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sendMessage",
      requestType = com.projeto1.grpc.RequestMessage.class,
      responseType = com.projeto1.grpc.ResponseMessage.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.projeto1.grpc.RequestMessage,
      com.projeto1.grpc.ResponseMessage> getSendMessageMethod() {
    io.grpc.MethodDescriptor<com.projeto1.grpc.RequestMessage, com.projeto1.grpc.ResponseMessage> getSendMessageMethod;
    if ((getSendMessageMethod = CommunicationServiceGrpc.getSendMessageMethod) == null) {
      synchronized (CommunicationServiceGrpc.class) {
        if ((getSendMessageMethod = CommunicationServiceGrpc.getSendMessageMethod) == null) {
          CommunicationServiceGrpc.getSendMessageMethod = getSendMessageMethod =
              io.grpc.MethodDescriptor.<com.projeto1.grpc.RequestMessage, com.projeto1.grpc.ResponseMessage>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sendMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.projeto1.grpc.RequestMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.projeto1.grpc.ResponseMessage.getDefaultInstance()))
              .setSchemaDescriptor(new CommunicationServiceMethodDescriptorSupplier("sendMessage"))
              .build();
        }
      }
    }
    return getSendMessageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CommunicationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CommunicationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CommunicationServiceStub>() {
        @java.lang.Override
        public CommunicationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CommunicationServiceStub(channel, callOptions);
        }
      };
    return CommunicationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CommunicationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CommunicationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CommunicationServiceBlockingStub>() {
        @java.lang.Override
        public CommunicationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CommunicationServiceBlockingStub(channel, callOptions);
        }
      };
    return CommunicationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CommunicationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CommunicationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CommunicationServiceFutureStub>() {
        @java.lang.Override
        public CommunicationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CommunicationServiceFutureStub(channel, callOptions);
        }
      };
    return CommunicationServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Define o serviço de comunicação que será implementado pelo servidor e chamado pelo cliente.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Define um método de chamada de procedimento remoto (RPC) chamado 'sendMessage'.
     * Ele aceita uma 'RequestMessage' como entrada e retorna uma 'ResponseMessage'.
     * É um RPC unário simples (uma requisição, uma resposta).
     * </pre>
     */
    default void sendMessage(com.projeto1.grpc.RequestMessage request,
        io.grpc.stub.StreamObserver<com.projeto1.grpc.ResponseMessage> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendMessageMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CommunicationService.
   * <pre>
   * Define o serviço de comunicação que será implementado pelo servidor e chamado pelo cliente.
   * </pre>
   */
  public static abstract class CommunicationServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CommunicationServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CommunicationService.
   * <pre>
   * Define o serviço de comunicação que será implementado pelo servidor e chamado pelo cliente.
   * </pre>
   */
  public static final class CommunicationServiceStub
      extends io.grpc.stub.AbstractAsyncStub<CommunicationServiceStub> {
    private CommunicationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CommunicationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CommunicationServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Define um método de chamada de procedimento remoto (RPC) chamado 'sendMessage'.
     * Ele aceita uma 'RequestMessage' como entrada e retorna uma 'ResponseMessage'.
     * É um RPC unário simples (uma requisição, uma resposta).
     * </pre>
     */
    public void sendMessage(com.projeto1.grpc.RequestMessage request,
        io.grpc.stub.StreamObserver<com.projeto1.grpc.ResponseMessage> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendMessageMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CommunicationService.
   * <pre>
   * Define o serviço de comunicação que será implementado pelo servidor e chamado pelo cliente.
   * </pre>
   */
  public static final class CommunicationServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CommunicationServiceBlockingStub> {
    private CommunicationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CommunicationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CommunicationServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Define um método de chamada de procedimento remoto (RPC) chamado 'sendMessage'.
     * Ele aceita uma 'RequestMessage' como entrada e retorna uma 'ResponseMessage'.
     * É um RPC unário simples (uma requisição, uma resposta).
     * </pre>
     */
    public com.projeto1.grpc.ResponseMessage sendMessage(com.projeto1.grpc.RequestMessage request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendMessageMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CommunicationService.
   * <pre>
   * Define o serviço de comunicação que será implementado pelo servidor e chamado pelo cliente.
   * </pre>
   */
  public static final class CommunicationServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<CommunicationServiceFutureStub> {
    private CommunicationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CommunicationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CommunicationServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Define um método de chamada de procedimento remoto (RPC) chamado 'sendMessage'.
     * Ele aceita uma 'RequestMessage' como entrada e retorna uma 'ResponseMessage'.
     * É um RPC unário simples (uma requisição, uma resposta).
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.projeto1.grpc.ResponseMessage> sendMessage(
        com.projeto1.grpc.RequestMessage request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendMessageMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_MESSAGE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_MESSAGE:
          serviceImpl.sendMessage((com.projeto1.grpc.RequestMessage) request,
              (io.grpc.stub.StreamObserver<com.projeto1.grpc.ResponseMessage>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSendMessageMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.projeto1.grpc.RequestMessage,
              com.projeto1.grpc.ResponseMessage>(
                service, METHODID_SEND_MESSAGE)))
        .build();
  }

  private static abstract class CommunicationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CommunicationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.projeto1.grpc.CommunicationProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CommunicationService");
    }
  }

  private static final class CommunicationServiceFileDescriptorSupplier
      extends CommunicationServiceBaseDescriptorSupplier {
    CommunicationServiceFileDescriptorSupplier() {}
  }

  private static final class CommunicationServiceMethodDescriptorSupplier
      extends CommunicationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    CommunicationServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CommunicationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CommunicationServiceFileDescriptorSupplier())
              .addMethod(getSendMessageMethod())
              .build();
        }
      }
    }
    return result;
  }
}
