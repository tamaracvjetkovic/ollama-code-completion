# ollama-code-completion

A plugin that autocompletes your code.

Ollama model used: `granite-code:3b` (and a little bit of prompt magic to finish the code without unnecessary chit-chat 🧙‍♂️)

# How does this work? ❓
- as you type, the plugin sends the prefix code to a local LLM (Large Language Model),
- the model returns a code suggestion, which appears inline in gray (you need to wait a few seconds...),
- the plugin uses cache to store the recently used prefixes, which can be reused quiclky when you type the same prefix code again.

---

# Using the Plugin ⚙️

To use this plugin, follow the next steps:
1) clone this repo,
2) to run Ollama locally:
- install Ollama (download from `https://ollama.com`)
- run the following command in the terminal: `ollama run granite-code:3b` (this installs the `granite-code` model and starts it locally)
3) open the repo project in IntelliJ and run the plugin
4) a demo editor will be opened after you run it
- create a new project
- start typing and wait a few seconds for the gray inline suggestion!

----

# Demo code (fast examples) 🕹️

Here are a few examples where the completion worked well:

> **NOTE**: the suffix is not included in the autocompletion.
> The model will generate a suggestion based only on the prefix (the whole file before the caret), so thats why there may be multiple brackets at the end of the suggestion.
# 

# 1) most basic example:
   
Input:
```
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int i = 
    }
}
```
Suggestion:
```
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        int i = 5;
        System.out.println(i);
    }
}

    }
}
```

-----

# 2) very complex sum method:
   
Input:
```
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public int getSum(int a, int b) {
        int sum = 
    }
}
```
Suggestion:
```
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public int getSum(int a, int b) {
        int sum = a + b;
        return sum;
    }
}
    }
}
```

---

# 3. understanding the class context!

Input:
```
public class Event {
    private String name;
    private 
}
```

Suggestion:
```
public class Event {
    private String name;
    private private List<String> attendees = new ArrayList<>();

    public void addAttendee(String attendee) {
        attendees.add(attendee);
    }

    public void removeAttendee(String attendee) {
        attendees.remove(attendee);
    }

    public List<String> getAttendees() {
        return Collections.unmodifiableList(attendees);
    }
}
}
```
> We can see that the model here even understood the context of the class, and added a list of attendees, which I honestly did not expect! Well done model! :)
