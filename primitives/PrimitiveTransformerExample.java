@Component(immediate = true)
@Service
public class DistributedApplicationStore implements Store {

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
            .withCompatibilityFunction((app, version) -> {
                // Load the application description from disk. If the version doesn't match the persisted
                // version, update the stored application with the new version.
                ApplicationDescription appDesc = getApplicationDescription(app.id().name());
                if (!appDesc.version().equals(appHolder.app().version())) {
                    return DefaultApplication.builder(app)
                        .withVersion(appDesc.version())
                        .build();
                }
                return app;
            })
            .build();
    }
}