@Component(immediate = true)
@Service
public class PrimitiveExample implements Store {

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected StorageService storageService;

    private ConsistentMap<ApplicationId, Application> apps;

    @Activate
    public void activate() {
        apps = storageService.<ApplicationId, Application>consistentMapBuilder()
            .withName("onos-apps")
            .withRelaxedReadConsistency()
            .withSerializer(Serializer.using(KryoNamespace.newBuilder()
                .register(KryoNamespaces.API)
                .register(ApplicationId.class)
                .register(Application.class)
                .register(Version.class)
                .register(ApplicationRole.class)
                .build()))
            .build();
    }

    public void storeApplication(Application app) {
        apps.put(app.id(), app);
    }

    public Application getApplication(ApplicationId appId) {
        return Versioned.valueOrNull(apps.get(appId));
    }
}