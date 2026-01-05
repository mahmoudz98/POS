# `:app`

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
  subgraph :sync
    direction TB
    :sync:work[work]:::android-library
  end
  subgraph :feature
    direction TB
    :feature:bill[bill]:::android-feature
    :feature:employee[employee]:::android-feature
    :feature:inventory[inventory]:::android-feature
    :feature:item[item]:::android-feature
    :feature:login[login]:::android-feature
    :feature:login-employee[login-employee]:::android-feature
    :feature:onboarding[onboarding]:::android-feature
    :feature:profile[profile]:::android-feature
    :feature:purchase[purchase]:::android-feature
    :feature:reports[reports]:::android-feature
    :feature:sale[sale]:::android-feature
    :feature:sales-report[sales-report]:::android-feature
    :feature:setting[setting]:::android-feature
    :feature:signout[signout]:::android-feature
    :feature:supplier[supplier]:::android-feature
  end
  subgraph :core
    direction TB
    :core:analytics[analytics]:::android-library
    :core:common[common]:::jvm-library
    :core:data[data]:::android-library
    :core:database[database]:::android-library
    :core:datastore[datastore]:::android-library
    :core:datastore_proto[datastore_proto]:::android-library
    :core:designsystem[designsystem]:::android-library
    :core:domain[domain]:::android-library
    :core:firebase[firebase]:::android-library
    :core:model[model]:::jvm-library
    :core:notifications[notifications]:::android-library
    :core:printer[printer]:::android-library
    :core:ui[ui]:::android-library
  end
  :benchmarks[benchmarks]:::android-test
  :app[app]:::android-application

  :app -.->|baselineProfile| :benchmarks
  :app -.-> :core:data
  :app -.-> :core:designsystem
  :app -.-> :core:ui
  :app -.-> :feature:bill
  :app -.-> :feature:employee
  :app -.-> :feature:inventory
  :app -.-> :feature:item
  :app -.-> :feature:login
  :app -.-> :feature:login-employee
  :app -.-> :feature:onboarding
  :app -.-> :feature:profile
  :app -.-> :feature:purchase
  :app -.-> :feature:reports
  :app -.-> :feature:sale
  :app -.-> :feature:sales-report
  :app -.-> :feature:setting
  :app -.-> :feature:signout
  :app -.-> :feature:supplier
  :app -.-> :sync:work
  :benchmarks -.->|testedApks| :app
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
  :core:printer -.-> :core:common
  :core:printer -.-> :core:firebase
  :core:printer -.-> :core:model
  :core:ui --> :core:analytics
  :core:ui --> :core:designsystem
  :core:ui --> :core:model
  :feature:bill -.-> :core:designsystem
  :feature:bill -.-> :core:domain
  :feature:bill -.-> :core:ui
  :feature:employee -.-> :core:designsystem
  :feature:employee -.-> :core:domain
  :feature:employee -.-> :core:ui
  :feature:inventory -.-> :core:designsystem
  :feature:inventory -.-> :core:ui
  :feature:item -.-> :core:designsystem
  :feature:item -.-> :core:domain
  :feature:item -.-> :core:ui
  :feature:login -.-> :core:designsystem
  :feature:login -.-> :core:domain
  :feature:login -.-> :core:ui
  :feature:login-employee -.-> :core:designsystem
  :feature:login-employee -.-> :core:domain
  :feature:login-employee -.-> :core:ui
  :feature:onboarding -.-> :core:designsystem
  :feature:onboarding -.-> :core:domain
  :feature:onboarding -.-> :core:ui
  :feature:profile -.-> :core:designsystem
  :feature:profile -.-> :core:domain
  :feature:profile -.-> :core:ui
  :feature:purchase -.-> :core:designsystem
  :feature:purchase -.-> :core:ui
  :feature:reports -.-> :core:designsystem
  :feature:reports -.-> :core:domain
  :feature:reports -.-> :core:ui
  :feature:sale -.-> :core:designsystem
  :feature:sale -.-> :core:domain
  :feature:sale -.-> :core:ui
  :feature:sales-report -.-> :core:designsystem
  :feature:sales-report -.-> :core:domain
  :feature:sales-report -.-> :core:ui
  :feature:setting -.-> :core:designsystem
  :feature:setting -.-> :core:domain
  :feature:setting -.-> :core:printer
  :feature:setting -.-> :core:ui
  :feature:signout -.-> :core:designsystem
  :feature:signout -.-> :core:domain
  :feature:signout -.-> :core:ui
  :feature:supplier -.-> :core:designsystem
  :feature:supplier -.-> :core:domain
  :feature:supplier -.-> :core:ui
  :sync:work -.-> :core:analytics
  :sync:work -.-> :core:common
  :sync:work -.-> :core:data
  :sync:work -.-> :core:database
  :sync:work -.-> :core:domain
  :sync:work -.-> :core:firebase

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
