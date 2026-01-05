# `:core:testing`

## Module dependency graph

<!--region graph-->
```mermaid
---
config:
  layout: elk
  elk:
    nodePlacementStrategy: SIMPLE
---
graph TB
  subgraph :core
    direction TB
    :core:analytics[analytics]:::android-library
    :core:common[common]:::jvm-library
    :core:data[data]:::android-library
    :core:database[database]:::android-library
    :core:datastore[datastore]:::android-library
    :core:datastore_proto[datastore_proto]:::android-library
    :core:domain[domain]:::android-library
    :core:firebase[firebase]:::android-library
    :core:model[model]:::jvm-library
    :core:notifications[notifications]:::android-library
    :core:testing[testing]:::android-library
  end

  :core:data -.-> :core:analytics
  :core:data --> :core:common
  :core:data -.-> :core:database
  :core:data --> :core:datastore
  :core:data --> :core:domain
  :core:data -.-> :core:firebase
  :core:database --> :core:model
  :core:datastore -.-> :core:common
  :core:datastore -.-> :core:datastore_proto
  :core:datastore --> :core:model
  :core:domain --> :core:model
  :core:domain --> :core:notifications
  :core:firebase -.-> :core:common
  :core:firebase -.-> :core:datastore
  :core:notifications -.-> :core:common
  :core:notifications --> :core:model
  :core:testing --> :core:data
  :core:testing --> :core:database
  :core:testing --> :core:firebase
  :core:testing --> :core:notifications

classDef android-application fill:#CAFFBF,stroke:#000,stroke-width:2px,color:#000;
classDef android-feature fill:#FFD6A5,stroke:#000,stroke-width:2px,color:#000;
classDef android-library fill:#9BF6FF,stroke:#000,stroke-width:2px,color:#000;
classDef android-test fill:#A0C4FF,stroke:#000,stroke-width:2px,color:#000;
classDef jvm-library fill:#BDB2FF,stroke:#000,stroke-width:2px,color:#000;
classDef unknown fill:#FFADAD,stroke:#000,stroke-width:2px,color:#000;
```

<details><summary>📋 Graph legend</summary>

```mermaid
graph TB
  application[application]:::android-application
  feature[feature]:::android-feature
  library[library]:::android-library
  jvm[jvm]:::jvm-library

  application -.-> feature
  library --> jvm

classDef android-application fill:#CAFFBF,stroke:#000,stroke-width:2px,color:#000;
classDef android-feature fill:#FFD6A5,stroke:#000,stroke-width:2px,color:#000;
classDef android-library fill:#9BF6FF,stroke:#000,stroke-width:2px,color:#000;
classDef android-test fill:#A0C4FF,stroke:#000,stroke-width:2px,color:#000;
classDef jvm-library fill:#BDB2FF,stroke:#000,stroke-width:2px,color:#000;
classDef unknown fill:#FFADAD,stroke:#000,stroke-width:2px,color:#000;
```

</details>
<!--endregion-->
