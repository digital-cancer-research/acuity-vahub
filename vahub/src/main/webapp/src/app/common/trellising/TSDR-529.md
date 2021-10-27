Приходит:

hasRandomisation -  timestampOption 

binableOption - добавляет бины
supportsDuration - нужно для AEs Over time Y оси (добавляет incl duration)
timestampOption - добавляет набор опций (        
DATE, DAYS_SINCE_FIRST_DOSE, WEEKS_SINCE_FIRST_DOSE, 

DAYS_SINCE_RANDOMIZATION, WEEKS_SINCE_RANDOMIZATION, - добавляются, если есть hasRandomization - true

DAYS_SINCE_FIRST_DOSE_OF_DRUG, WEEKS_SINCE_FIRST_DOSE_OF_DRUG - добавляются, если есть хоть 1 драг)

```
"settings":{
    "settings":{
        "options":{
            "Y_AXIS":{ 
                "groupByOption":"ACTUAL_VALUE", //опция value из y-axis response
                "params":null 
            },
            "X_AXIS":{ //
                "groupByOption":"MEASUREMENT_TIME_POINT",  //опция value из x-axis response
                "params":{  //опции, сгенерированные на UI
                    "TIMESTAMP_TYPE":"DAYS_SINCE_FIRST_DOSE", //добавленная опция на UI
                    (выбрана сейчас в селекторе и видна юзеру)
                    "BIN_SIZE":10 //бины, видные юзеру
                }
            }
        },
        "trellisOptions":[
            {
                "groupByOption":"MEASUREMENT",
                "params": null
            }
        ]
    },
    "filterByTrellisOptions":[
        {
            "MEASUREMENT":"Alanine Aminotransferase (IU/L)"
        },
        {
            "MEASUREMENT":"Alanine Aminotransferase (IU/L)"
            ARM: 'arm1'
        },
        {
            MEASUREMENT: 'some lab code',
            ARM: 'arm2'
        },
        {
            MEASUREMENT: 'another lab code',
            ARM: 'arm1'
        },
        {
            MEASUREMENT: 'another lab code',
            ARM: 'arm2'
        }
    ],
}
```


