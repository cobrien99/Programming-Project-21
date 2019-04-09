import json
import csv
from watson_developer_cloud import NaturalLanguageUnderstandingV1
from watson_developer_cloud.natural_language_understanding_v1 import Features, CategoriesOptions, KeywordsOptions, RelationsOptions, EntitiesOptions

natural_language_understanding = NaturalLanguageUnderstandingV1(
    version='2018-11-16',
    iam_apikey='TZ7yEzVmBp5wy4aIA-0WrxeBrlZttx0I46ht_9bBFOGm',
    url='https://gateway-wdc.watsonplatform.net/natural-language-understanding/api'
)

f = open('NLC csv training data1.2.csv')
csv_f = csv.reader(f)

with open('output.csv', 'w') as f:
    thewriter = csv.writer(f)
    for row in csv_f:
        print row[0]

        response = natural_language_understanding.analyze(
        text = row[0],
        features=Features(entities=EntitiesOptions(sentiment=True))).get_result()

        data = json.dumps(response, indent=2)
        y = json.loads(data)
        print(data)

        count = 0
        
        try:
            while(count < 10):    
                if (y['entities'][count]['type'] == 'Location' or y['entities'][count]['type'] == 'Facility'):
                    locationType = y['entities'][count]['disambiguation']['subtype'][0] 
                    print(locationType)
                    locationName = y['entities'][count]['text']
                    print(locationName)
                    thewriter.writerow([row[0], locationType, locationName])
                count = count + 1

        except:
            if(count > 0):
                print("------------------------------------")
            else:
                print("No location found in text.")



