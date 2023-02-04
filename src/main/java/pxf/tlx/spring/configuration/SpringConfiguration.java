package pxf.tlx.spring.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import pxf.tl.util.ToolString;
import pxf.tlx.basesystem.data.entity.SystemCors;
import pxf.tlx.basesystem.action.SystemAction;
import pxf.tlx.spring.web.request.RequestDataMethodArgumentResolver;
import pxf.tlx.spring.web.response.ResponseResultHandler;
import pxf.tlx.spring.web.response.ResponseViewHandler;
import pxf.tlx.spring.web.response.exception.GlobalExceptionBodyAdvice;
import pxf.tlx.spring.web.response.exception.GlobalExceptionBodyHandler;
import pxf.tlx.spring.web.response.exception.GlobalExceptionPageAdvice;
import pxf.tlx.spring.web.response.exception.GlobalExceptionPageHandler;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author potatoxf
 */
@Order
@Getter
@Setter
@EnableWebMvc
public abstract class SpringConfiguration
        implements WebMvcConfigurer, ApplicationContextAware {

    /**
     * Spring应用内容
     */
    private static ApplicationContext applicationContext;

    /**
     * bean类型排序
     */
    private Map<Class<?>, Map<Class<?>, Integer>> beanTypeSorted;

    /**
     * bean名称排序
     */
    private Map<Class<?>, Map<String, Integer>> beanNameSorted;

    private static ApplicationContext currentApplicationContext() {
        return Objects.requireNonNull(applicationContext, "The SpringConfiguration not autowired bean");
    }

    public static String getId() {
        return currentApplicationContext().getId();
    }

    @Nonnull
    public static String getApplicationName() {
        return currentApplicationContext().getApplicationName();
    }

    @Nonnull
    public static String getDisplayName() {
        return currentApplicationContext().getDisplayName();
    }

    public static long getStartupDate() {
        return currentApplicationContext().getStartupDate();
    }

    public static ApplicationContext getParent() {
        return currentApplicationContext().getParent();
    }

    @Nonnull
    public static AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return currentApplicationContext().getAutowireCapableBeanFactory();
    }

    public static BeanFactory getParentBeanFactory() {
        return currentApplicationContext().getParentBeanFactory();
    }

    public static boolean containsLocalBean(@Nonnull String name) {
        return currentApplicationContext().containsLocalBean(name);
    }

    public static boolean containsBeanDefinition(@Nonnull String beanName) {
        return currentApplicationContext().containsBeanDefinition(beanName);
    }

    public static int getBeanDefinitionCount() {
        return currentApplicationContext().getBeanDefinitionCount();
    }

    @Nonnull
    public static String[] getBeanDefinitionNames() {
        return currentApplicationContext().getBeanDefinitionNames();
    }

    @Nonnull
    public static <T> ObjectProvider<T> getBeanProvider(@Nonnull Class<T> requiredType, boolean allowEagerInit) {
        return currentApplicationContext().getBeanProvider(requiredType, allowEagerInit);
    }

    @Nonnull
    public static <T> ObjectProvider<T> getBeanProvider(@Nonnull ResolvableType requiredType, boolean allowEagerInit) {
        return currentApplicationContext().getBeanProvider(requiredType, allowEagerInit);
    }

    @Nonnull
    public static String[] getBeanNamesForType(@Nonnull ResolvableType type) {
        return currentApplicationContext().getBeanNamesForType(type);
    }

    @Nonnull
    public static String[] getBeanNamesForType(@Nonnull ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        return currentApplicationContext().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Nonnull
    public static String[] getBeanNamesForType(Class<?> type) {
        return currentApplicationContext().getBeanNamesForType(type);
    }

    @Nonnull
    public static String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return currentApplicationContext().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Nonnull
    public static <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return currentApplicationContext().getBeansOfType(type);
    }

    @Nonnull
    public static <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        return currentApplicationContext().getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    @Nonnull
    public static String[] getBeanNamesForAnnotation(@Nonnull Class<? extends Annotation> annotationType) {
        return currentApplicationContext().getBeanNamesForAnnotation(annotationType);
    }

    @Nonnull
    public static Map<String, Object> getBeansWithAnnotation(@Nonnull Class<? extends Annotation> annotationType) throws BeansException {
        return currentApplicationContext().getBeansWithAnnotation(annotationType);
    }

    public static <A extends Annotation> A findAnnotationOnBean(@Nonnull String beanName, @Nonnull Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return currentApplicationContext().findAnnotationOnBean(beanName, annotationType);
    }

    public static <A extends Annotation> A findAnnotationOnBean(@Nonnull String beanName, @Nonnull Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return currentApplicationContext().findAnnotationOnBean(beanName, annotationType, allowFactoryBeanInit);
    }

    @Nonnull
    public static Object getBean(@Nonnull String name) throws BeansException {
        return currentApplicationContext().getBean(name);
    }

    @Nonnull
    public static <T> T getBean(@Nonnull String name, @Nonnull Class<T> requiredType) throws BeansException {
        return currentApplicationContext().getBean(name, requiredType);
    }

    @Nonnull
    public static Object getBean(@Nonnull String name, @Nonnull Object... args) throws BeansException {
        return currentApplicationContext().getBean(name, args);
    }

    @Nonnull
    public static <T> T getBean(@Nonnull Class<T> requiredType) throws BeansException {
        return currentApplicationContext().getBean(requiredType);
    }

    @Nonnull
    public static <T> T getBean(@Nonnull Class<T> requiredType, @Nonnull Object... args) throws BeansException {
        return currentApplicationContext().getBean(requiredType, args);
    }

    @Nonnull
    public static <T> ObjectProvider<T> getBeanProvider(@Nonnull Class<T> requiredType) {
        return currentApplicationContext().getBeanProvider(requiredType);
    }

    @Nonnull
    public static <T> ObjectProvider<T> getBeanProvider(@Nonnull ResolvableType requiredType) {
        return currentApplicationContext().getBeanProvider(requiredType);
    }

    public static boolean containsBean(@Nonnull String name) {
        return currentApplicationContext().containsBean(name);
    }

    public static boolean isSingleton(@Nonnull String name) throws NoSuchBeanDefinitionException {
        return currentApplicationContext().isSingleton(name);
    }

    public static boolean isPrototype(@Nonnull String name) throws NoSuchBeanDefinitionException {
        return currentApplicationContext().isPrototype(name);
    }

    public static boolean isTypeMatch(@Nonnull String name, @Nonnull ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return currentApplicationContext().isTypeMatch(name, typeToMatch);
    }

    public static boolean isTypeMatch(@Nonnull String name, @Nonnull Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return currentApplicationContext().isTypeMatch(name, typeToMatch);
    }

    public static Class<?> getType(@Nonnull String name) throws NoSuchBeanDefinitionException {
        return currentApplicationContext().getType(name);
    }

    public static Class<?> getType(@Nonnull String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return currentApplicationContext().getType(name, allowFactoryBeanInit);
    }

    @Nonnull
    public static String[] getAliases(@Nonnull String name) {
        return currentApplicationContext().getAliases(name);
    }

    public static void publishEvent(@Nonnull Object event) {
        currentApplicationContext().publishEvent(event);
    }

    public static String getMessage(@Nonnull String code, Object[] args, String defaultMessage, @Nonnull Locale locale) {
        return currentApplicationContext().getMessage(code, args, defaultMessage, locale);
    }

    @Nonnull
    public static String getMessage(@Nonnull String code, Object[] args, @Nonnull Locale locale) throws NoSuchMessageException {
        return currentApplicationContext().getMessage(code, args, locale);
    }

    @Nonnull
    public static String getMessage(@Nonnull MessageSourceResolvable resolvable, @Nonnull Locale locale) throws NoSuchMessageException {
        return currentApplicationContext().getMessage(resolvable, locale);
    }

    @Nonnull
    public static Environment getEnvironment() {
        return currentApplicationContext().getEnvironment();
    }

    @Nonnull
    public static Resource[] getResources(@Nonnull String locationPattern) throws IOException {
        return currentApplicationContext().getResources(locationPattern);
    }

    @Nonnull
    public static Resource getResource(@Nonnull String location) {
        return currentApplicationContext().getResource(location);
    }

    @Nonnull
    public static ClassLoader getClassLoader() {
        return currentApplicationContext().getClassLoader();
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        SpringConfiguration.applicationContext = applicationContext;
    }

    /**
     * 响应结果处理器
     *
     * @return {@code ResponseResultHandler}
     */
    public ResponseResultHandler responseResultHandler() {
        return new ResponseResultHandler();
    }

    /**
     * 响应视图处理器
     *
     * @return {@code ResponseResultHandler}
     */
    public ResponseViewHandler responseViewHandler() {
        return new ResponseViewHandler();
    }

    /**
     * 请求数据方法参数解析器
     *
     * @return {@code RequestDataMethodArgumentResolver}
     */
    public RequestDataMethodArgumentResolver requestDataMethodArgumentResolver(RequestResponseBodyMethodProcessor formResolver,
                                                                               ServletModelAttributeMethodProcessor jsonResolver) {
        return new RequestDataMethodArgumentResolver(formResolver, jsonResolver);
    }

    /**
     * 全局异常返回体处理器
     *
     * @param globalExceptionBodyHandler 返回体处理器
     * @return {@code GlobalExceptionBodyAdvice}
     */
    public GlobalExceptionBodyAdvice globalExceptionBodyAdvice(GlobalExceptionBodyHandler globalExceptionBodyHandler) {
        return new GlobalExceptionBodyAdvice(globalExceptionBodyHandler);
    }

    /**
     * 全局异常返回页面处理器
     *
     * @param globalExceptionPageHandler 返回页面处理器
     * @return {@code GlobalExceptionPageAdvice}
     */
    public GlobalExceptionPageAdvice globalExceptionPageAdvice(GlobalExceptionPageHandler globalExceptionPageHandler) {
        return new GlobalExceptionPageAdvice(globalExceptionPageHandler);
    }

    /**
     * 根据配置{@code SystemCors}生成{@code CorsFilter}
     *
     * @param systemAction {@code SystemAction}
     * @return {@code CorsFilter}
     */
    public CorsFilter corsFilter(SystemAction systemAction) {
        List<String> any = List.of("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        List<SystemCors> systemCorsList = systemAction.listCors();
        for (SystemCors systemCors : systemCorsList) {
            CorsConfiguration configuration = new CorsConfiguration();
            List<String> originList = ToolString.split(systemCors.getOrigin(), ",", true, true);
            configuration.setAllowedOrigins(originList == null ? any : originList);
            List<String> methodsList = ToolString.split(systemCors.getMethods(), ",", true, true);
            configuration.setAllowedMethods(methodsList == null ? any : methodsList);
            List<String> headersList = ToolString.split(systemCors.getHeaders(), ",", true, true);
            configuration.setAllowedHeaders(headersList == null ? any : headersList);
            configuration.setAllowCredentials(systemCors.isCredentials());
            Long maxAge = systemCors.getMaxAge();
            if (maxAge != null) {
                configuration.setMaxAge(maxAge);
            }
            source.registerCorsConfiguration(systemCors.getPattern(), configuration);
        }
        return new CorsFilter(source);
    }

    @Override
    public void addReturnValueHandlers(@Nonnull List<HandlerMethodReturnValueHandler> handlers) {
        // 找出Bean容器中所有的 {@code HandlerMethodReturnValueHandler}
        List<HandlerMethodReturnValueHandler> list =
                getSpringContainerBeanSortedList(HandlerMethodReturnValueHandler.class, handlers);
        handlers.clear();
        handlers.addAll(list);
    }

    @Override
    public void configureHandlerExceptionResolvers(@Nonnull List<HandlerExceptionResolver> resolvers) {
        // 找出Bean容器中所有的 {@code HandlerExceptionResolver}
        List<HandlerExceptionResolver> list =
                getSpringContainerBeanSortedList(HandlerExceptionResolver.class, resolvers);
        resolvers.clear();
        resolvers.addAll(list);
    }

    @Override
    public void configureMessageConverters(@Nonnull List<HttpMessageConverter<?>> converters) {
        // 找出Bean容器中所有的 {@code HttpMessageConverter}
        List<?> list = getSpringContainerBeanSortedList(HttpMessageConverter.class, converters);
        converters.clear();
        list.forEach(o -> converters.add((HttpMessageConverter<?>) o));
    }

    @Override
    public void addArgumentResolvers(@Nonnull List<HandlerMethodArgumentResolver> resolvers) {
        // 找出Bean容器中所有的 {@code HandlerMethodArgumentResolver}
        List<HandlerMethodArgumentResolver> list =
                getSpringContainerBeanSortedList(HandlerMethodArgumentResolver.class, resolvers);
        resolvers.clear();
        resolvers.addAll(list);
    }

    /**
     * 获取Spring容器中某类Bean并排序
     *
     * @param clz                 指定类
     * @param builtinInstanceList 初始列表
     * @param <T>                 指定类类型
     * @return 返回排序后的Bean列表
     */
    @Nonnull
    protected <T> List<T> getSpringContainerBeanSortedList(
            @Nonnull Class<T> clz, @Nonnull List<? extends T> builtinInstanceList) {
        Map<String, T> beansOfType = applicationContext.getBeansOfType(clz);
        //根据注解进行排序
        List<T> list =
                beansOfType.values().stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted(new AnnotationAwareOrderComparator())
                        .collect(Collectors.toList());

        if (!builtinInstanceList.isEmpty()) {
            for (T t : builtinInstanceList) {
                if (list.contains(t)) {
                    continue;
                }
                list.add(t);
            }
        }

        Set<T> result = new LinkedHashSet<>(list.size());

        //根据beanName排序
        if (beanNameSorted != null) {
            Map<String, Integer> beanNameSortedMap = beanNameSorted.get(clz);

            if (beanNameSortedMap != null) {

                List<String> beanNameList =
                        beansOfType.keySet().stream()
                                .filter(beanNameSortedMap::containsKey)
                                .sorted(Comparator.comparingInt(beanNameSortedMap::get))
                                .collect(Collectors.toList());

                for (String beanName : beanNameList) {
                    result.add(beansOfType.get(beanName));
                }
            }
        }


        list.removeAll(result);

        //根据bean类型排序
        if (beanTypeSorted != null) {
            Map<Class<?>, Integer> beanTypeSortedMap = beanTypeSorted.get(clz);
            if (beanTypeSortedMap != null) {

                List<Class<?>> beanTypeList =
                        list.stream()
                                .map(Object::getClass)
                                .distinct()
                                .filter(beanTypeSortedMap::containsKey)
                                .sorted(Comparator.comparingInt(beanTypeSortedMap::get))
                                .collect(Collectors.toList());

                for (Class<?> beanType : beanTypeList) {
                    List<T> beanTypeFilter =
                            list.stream().filter(t -> t.getClass() == beanType).collect(Collectors.toList());

                    list.removeAll(beanTypeFilter);
                    result.addAll(beanTypeFilter);
                }
            }
        }

        if (!list.isEmpty()) {
            result.addAll(list);
        }
        return new ArrayList<>(result);
    }
}
