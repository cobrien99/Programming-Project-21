import json
import csv
from watson_developer_cloud import NaturalLanguageUnderstandingV1
from watson_developer_cloud.natural_language_understanding_v1 import Features, CategoriesOptions, KeywordsOptions, RelationsOptions, EntitiesOptions

natural_language_understanding = NaturalLanguageUnderstandingV1(
    version='2018-11-16',
    iam_apikey='TZ7yEzVmBp5wy4aIA-0WrxeBrlZttx0I46ht_9bBFOGm',
    url='https://gateway-wdc.watsonplatform.net/natural-language-understanding/api'
)

f = open('NLC csv training data1.2.csv')      #input CSV file to read from
csv_f = csv.reader(f)       

with open('output.csv', 'w') as f:      #open output CSV file to write the locations to
    thewriter = csv.writer(f)
    for row in csv_f:           #loop through all text found in input CSV file
        print row[0]        #print text to console 

        response = natural_language_understanding.analyze(
        text = row[0],      
        features=Features(entities=EntitiesOptions(sentiment=True))).get_result()       #get NLU to analyze the text and return the entities (objects) found in the text

        data = json.dumps(response, indent=2)       #converts json results from NLU to String 
        y = json.loads(data)        #allows for json to be queried
        print(data)         #print json results from NLU

        count = 0 
        
        try:        #try to find location in text 
            while(count < 10):      #count for looking for multiple locations found in text (10 can be changed to whatever limit of locations user wants the program to be able to find per text i.e. if there are 12 locations in a text then the program will only display 10)
                if (y['entities'][count]['type'] == 'Location' or y['entities'][count]['type'] == 'Facility'):      #if location or facility is found in text 
                    locationType = y['entities'][count]['disambiguation']['subtype'][0]         #variable for type of location i.e. city, country, administrative district etc...
                    print(locationType)         
                    locationName = y['entities'][count]['text']         #variable for name of location i.e. India, Dublin Airport etc...
                    print(locationName)
                    thewriter.writerow([row[0], locationType, locationName])        #write a row to output CSV file containing 3 coloumns - text, type of location, name of location
                count = count + 1       

        except:         #catch if location is not found
            if(count > 0):
                print("------------------------------------")       #for console display purposes
            else:
                print("No location found in text.")        