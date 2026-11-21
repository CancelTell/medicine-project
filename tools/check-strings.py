#!/usr/bin/env python3
from collections import Counter
from pathlib import Path
import sys
import xml.etree.ElementTree as ET


def read_keys(path):
    root = ET.parse(path).getroot()
    return Counter(
        (element.tag, element.attrib["name"])
        for element in root
        if "name" in element.attrib
    )


def check_strings(resources):
    base_path = resources / "values" / "strings.xml"
    paths = [base_path] + sorted(resources.glob("values-*/strings.xml"))
    keys_by_path = {}
    errors = []
    for path in paths:
        try:
            keys = read_keys(path)
        except (OSError, ET.ParseError) as error:
            errors.append(f"{path}: {error}")
            continue
        keys_by_path[path] = set(keys)
        for (tag, name), count in sorted(keys.items()):
            if count > 1:
                errors.append(f"{path.parent.name}: duplicate {tag}/{name}")

    if base_path in keys_by_path:
        base = keys_by_path[base_path]
        for path, keys in keys_by_path.items():
            if path == base_path:
                continue
            for tag, name in sorted(base - keys):
                errors.append(f"{path.parent.name}: missing {tag}/{name}")
            for tag, name in sorted(keys - base):
                errors.append(f"{path.parent.name}: extra {tag}/{name}")
    return errors


def main():
    resources = (
        Path(__file__).resolve().parents[1]
        / "shared/src/commonMain/composeResources"
    )
    errors = check_strings(resources)
    for error in errors:
        print(error)
    print(f"Расхождений: {len(errors)}")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
